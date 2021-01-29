import com.google.common.collect.ImmutableMap;
import files.Benchmark;
import files.FileUtils;
import graph.BFAnetwork.*;
import graph.bfa.BFA;
import graph.bfa.BFABuilder;
import graph.bfa.EventTransition;
import graph.fa.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class BFANetworkTest {

    // nodes and transitions of BFA C2 from page 24
    private static final FAState s20 = new StateBuilder("20").build();
    private static final FAState s21 = new StateBuilder("21").build();
    private static final EventTransition t2a = new EventTransition.Builder("t2a").inEvent("e2(L2)")
            .addOutEvent("e3(L3)").observabilityLabel("o2").build();
    private static final EventTransition t2b = new EventTransition.Builder("t2b").addOutEvent("e3(L3)")
            .relevanceLabel("r").build();

    // nodes and transitions of BFA C3 from page 24
    private static final FAState s30 = new StateBuilder("30").build();
    private static final FAState s31 = new StateBuilder("31").build();
    private static final EventTransition t3a = new EventTransition.Builder("t3a").addOutEvent("e2(L2)")
            .observabilityLabel("o3").build();
    private static final EventTransition t3b = new EventTransition.Builder("t3b").inEvent("e3(L3)").build();
    private static final EventTransition t3c = new EventTransition.Builder("t3c").inEvent("e3(L3)").relevanceLabel("f")
            .build();

    // links in the BFA Network's topology of page 26
    private Link l2 = new Link("L2");
    private Link l3 = new Link("L3");

    // bfas in the network
    private BFA c2;
    private BFA c3;

    // the bfaNetwork to test
    private BFANetwork bfaNetwork;

    /**
     * @return the behavioral FA C2 from page 24
     */
    private final static BFA BFAc2FromPage24() {
        return new BFABuilder("C2").putInitialState(s20).putTransition(s20, s21, t2a).putTransition(s21, s20, t2b)
                .build();
    }

    /**
     * @return the behavioral FA C3 from page 24
     */
    private final static BFA BFAc3FromPage24() {
        return new BFABuilder("C3").putInitialState(s30).putTransition(s30, s31, t3a).putTransition(s31, s30, t3b)
                .putTransition(s31, s31, t3c).build();
    }

    /**
     * @return the behavioral FA Network from page 26
     */
    private BFANetwork BFANetworkFromPage26() {
        c2 = BFAc2FromPage24();
        c3 = BFAc3FromPage24();
        return new BFANetworkBuilder().putLink(c3, c2, l2).putLink(c2, c3, l3).build();
    }

    @BeforeEach
    public void setUp() {
        bfaNetwork = BFANetworkFromPage26();
    }

    @Test
    public void t3aShouldBeEnabled() {
        Set<EventTransition> transitionsEnabledInC3 = BFANetworkSupervisor.getTransitionsEnabledInBfa(bfaNetwork, c3);
        assertTrue(transitionsEnabledInC3.contains(t3a));
    }

    @Test
    public void executeTransitionT3a() {
        BFANetworkSupervisor.executeTransition(bfaNetwork, c3, t3a);
        assertSame(c3.getCurrentState(), s31);
        assertSame(c2.getCurrentState(), s20);
        assertEquals("e2(L2)", l2.getEvent().get());
        assertTrue(l3.getEvent().isEmpty());
    }

    @Test
    public void checkIfTransitionT2aIsEnabledAfterT3A() {
        BFANetworkSupervisor.executeTransition(bfaNetwork, c3, t3a);
        Set<EventTransition> transitionsEnabledInC2 = BFANetworkSupervisor.getTransitionsEnabledInBfa(bfaNetwork, c2);
        assertTrue(transitionsEnabledInC2.contains(t2a));
    }

    @Test
    public void executeTransitionT2aAfterT3A() {
        BFANetworkSupervisor.executeTransition(bfaNetwork, c3, t3a);
        BFANetworkSupervisor.executeTransition(bfaNetwork, c2, t2a);
        assertSame(c3.getCurrentState(), s31);
        assertSame(c2.getCurrentState(), s21);
        assertTrue(l2.getEvent().isEmpty());
        assertEquals("e3(L3)", l3.getEvent().get());
    }

    @Test
    public void itShouldGetTheBFANetworkState() {
        BSState state = BFANetworkSupervisor.getBFANetworkState(bfaNetwork);
        Map<BFA, FAState> bfas = new HashMap<>();
        Map<Link, String> links = new HashMap<>();
        bfas.put(c2, s20);
        bfas.put(c3, s30);
        links.put(l2, null);
        links.put(l3, null);
        assertEquals(bfas, state.getBfas());
        assertEquals(links, state.getLinks());

    }

    @Test
    public void itShouldRollback() {
        BSState oldState = BFANetworkSupervisor.getBFANetworkState(bfaNetwork);
        BFANetworkSupervisor.executeTransition(bfaNetwork, c3, t3a);
        BFANetworkSupervisor.rollbackBFANetwork(oldState);
        BSState newState = BFANetworkSupervisor.getBFANetworkState(bfaNetwork);

        assertEquals(oldState, newState);
    }

    @Test
    public void computeBehavioralSpaceOfNetwork() {
        FA<BSState, BSTransition> space = BFANetworkSupervisor.getBehavioralSpace(bfaNetwork);

        // there should 15 states
        assertEquals(15, space.getStates().size());

        // there should be 4 final states
        assertEquals(4, space.getFinalStates().size());

        // there should be 18 transitions
        assertEquals(18, space.getTransitions().size());
    }

    @Test
    public void computeBehavioralSpaceOfLinearObservation() {
        ArrayList<String> linearObservation = new ArrayList<>();
        linearObservation.add("o3");
        linearObservation.add("o2");

        FA<LOBSState, BSTransition> space = null;
        try {
            space = BFANetworkSupervisor.getBehavioralSpaceForLinearObservation(bfaNetwork, linearObservation);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        // Before pruning
        // there should be 9 states
        assertEquals(9, space.getStates().size());

        // there should be 4 final states
        assertEquals(4, space.getFinalStates().size());

        // there should be 8 transitions
        assertEquals(8, space.getTransitions().size());

        BFANetworkSupervisor.pruneFA(space);

        // After pruning
        // there should be 8 states
        assertEquals(8, space.getStates().size());

        /*
         * Set<LOBSState> finalStates = space.getAcceptanceStates(); for (LOBSState
         * state : finalStates) { state.isAcceptance(true); }
         */

        Set<String> acceptedLanguages = AcceptedLanguages.reduceFAtoMultipleRegex(space);

        Set<String> realAcceptedLanguages = Set.of("", "f", "fr", "frf");

        assertEquals(acceptedLanguages, realAcceptedLanguages);

    }

    /**
     * Check that the BS of page 35-36 is pruned correctly
     */
    @Test
    public void itShouldPruneBehavioralSpace() {
        FA<BSState, BSTransition> space = BFANetworkSupervisor.getBehavioralSpace(bfaNetwork);
        assertEquals(15, space.getStates().size(), "The BS of page 35 should have 15 states");
        BFANetworkSupervisor.pruneFA(space);
        assertEquals(13, space.getStates().size(), "The BS after pruning should have 13 states");
    }

    /**
     * Check that a FA containing a loop of non-final states gets pruned correctly
     * by removing that loop. The FA under test is the following:
     *
     * s0 --> s1, s1 --> s2, s2 --> s1
     *
     * where: initial state: s0 final states: {s0}
     *
     * The expected FA after pruning should contain only s0.
     */
    @Test
    public void itShouldPruneFAWithLoopOfNonFinalStates() {
        FAState s0 = new StateBuilder("s0").build();
        FAState s1 = new StateBuilder("s1").build();
        FAState s2 = new StateBuilder("s2").build();
        FA<FAState, Transition> space = new FABuilder<FAState, Transition>().putInitialState(s0).putAcceptanceState(s0)
                .putFinalState(s0).putTransition(s0, s1, new Transition("")).putTransition(s1, s2, new Transition(""))
                .putTransition(s2, s1, new Transition("")).build();

        BFANetworkSupervisor.pruneFA(space);
        assertTrue(space.getStates().contains(s0), "The BS after pruning should contain s0");
        assertFalse(space.getStates().contains(s1), "The BS after pruning should not contain s1");
        assertFalse(space.getStates().contains(s2), "The BS after pruning should not contain s2");
    }

    /**
     * @return the behavioral space showed at page 38 of the project description. It
     *         is computed on the network of bfas from page 26.
     */
    private FA<BSState, BSTransition> behavioralSpaceFromPage38() {

        // compute behavioral space of network of page 26
        FA<BSState, BSTransition> bs = BFANetworkSupervisor.getBehavioralSpace(BFANetworkFromPage26());
        // prune it
        BFANetworkSupervisor.pruneFA(bs);

        // rename states like at page 38
        bs.getNode("20 30 eps eps").orElseThrow().setName("0");
        bs.getNode("20 31 e2(L2) eps").orElseThrow().setName("1");
        bs.getNode("21 31 eps e3(L3)").orElseThrow().setName("2");
        bs.getNode("21 31 eps eps").orElseThrow().setName("3");
        bs.getNode("20 31 eps e3(L3)").orElseThrow().setName("4");
        bs.getNode("20 31 eps eps").orElseThrow().setName("5");
        bs.getNode("21 30 eps eps").orElseThrow().setName("6");
        bs.getNode("20 30 eps e3(L3)").orElseThrow().setName("7");
        bs.getNode("20 31 e2(L2) e3(L3)").orElseThrow().setName("8");
        bs.getNode("20 30 e2(L2) eps").orElseThrow().setName("9");
        bs.getNode("21 30 eps e3(L3)").orElseThrow().setName("10");
        bs.getNode("21 31 e2(L2) e3(L3)").orElseThrow().setName("11");
        bs.getNode("21 31 e2(L2) eps").orElseThrow().setName("12");

        return bs;
    }

    @Test
    public void itShouldThrowExceptionWhenComputingSilentClosureOfStateWithoutObservableIngoingTransitions() {
        FA<BSState, BSTransition> bs = behavioralSpaceFromPage38();

        // get a non-initial state which hasn't any observable ingoing transitions.
        BSState nonObservableState = null;
        for (BSState s : bs.getStates()) {
            if (!bs.isInitial(s)
                    && bs.getNetwork().inEdges(s).stream().noneMatch(BSTransition::hasObservabilityLabel)) {
                nonObservableState = s;
                break;
            }
        }

        BSState finalNonObservableState = nonObservableState;
        assertThrows(IllegalArgumentException.class,
                () -> BFANetworkSupervisor.silentClosure(bs, finalNonObservableState));
    }

    @Test
    public void itShouldComputeSilentClosure() {
        FA<BSState, BSTransition> bs = behavioralSpaceFromPage38();

        // get state 2
        BSState s2 = bs.getNode("2").orElseThrow();
        FA<BSState, BSTransition> silentClosure = BFANetworkSupervisor.silentClosure(bs, s2);

        assertTrue(silentClosure.getInitialState().equals(bs.getNode("2").orElseThrow()));
        assertTrue(silentClosure.getAcceptanceStates().contains(bs.getNode("3").orElseThrow()));
        assertTrue(silentClosure.getAcceptanceStates().contains(bs.getNode("6").orElseThrow()));
        assertTrue(silentClosure.getAcceptanceStates().contains(bs.getNode("7").orElseThrow()));
        assertTrue(silentClosure.getAcceptanceStates().contains(bs.getNode("5").orElseThrow()));
        assertTrue(silentClosure.getAcceptanceStates().contains(bs.getNode("0").orElseThrow()));
        assertFalse(silentClosure.getStates().contains(bs.getNode("1").orElseThrow()));
        assertFalse(silentClosure.getStates().contains(bs.getNode("8").orElseThrow()));
    }

    @Test
    public void itShouldComputeDecorationOfSilentClosure() {
        FA<BSState, BSTransition> bs = behavioralSpaceFromPage38();

        // get state 2
        BSState s2 = bs.getNode("2").orElseThrow();
        FA<BSState, BSTransition> silentClosure = BFANetworkSupervisor.silentClosure(bs, s2);
        FA<DBSState, BSTransition> decoratedSilentClosure = BFANetworkSupervisor.decoratedSilentClosure(silentClosure);

        assertEquals("f", decoratedSilentClosure.getNode("3").orElseThrow().getDecoration());
        assertEquals("fr", decoratedSilentClosure.getNode("0").orElseThrow().getDecoration());
        assertEquals("r", decoratedSilentClosure.getNode("7").orElseThrow().getDecoration());
        assertEquals("frf", decoratedSilentClosure.getNode("5").orElseThrow().getDecoration());
        assertEquals("", decoratedSilentClosure.getNode("6").orElseThrow().getDecoration());
    }

    @Test
    public void itShouldComputeDiagnosisOfSilentClosureX2() {
        FA<BSState, BSTransition> bs = behavioralSpaceFromPage38();
        // get state 2
        BSState s2 = bs.getNode("2").orElseThrow();
        FA<BSState, BSTransition> silentClosure = BFANetworkSupervisor.silentClosure(bs, s2);
        // compute decoration of page 61
        FA<DBSState, BSTransition> decoratedSilentClosure = BFANetworkSupervisor.decoratedSilentClosure(silentClosure);
        Map<DBSState, String> expectedDiagnosis = ImmutableMap.of(decoratedSilentClosure.getNode("3").orElseThrow(),
                "f", decoratedSilentClosure.getNode("0").orElseThrow(), "fr",
                decoratedSilentClosure.getNode("5").orElseThrow(), "frf",
                decoratedSilentClosure.getNode("6").orElseThrow(), "");

        assertEquals(expectedDiagnosis, BFANetworkSupervisor.diagnosis(decoratedSilentClosure));
    }

    @Test
    public void itShouldComputeDiagnosisOfSilentClosureX0() {
        FA<BSState, BSTransition> bs = behavioralSpaceFromPage38();
        // get state 0
        BSState s0 = bs.getNode("0").orElseThrow();
        FA<BSState, BSTransition> silentClosure = BFANetworkSupervisor.silentClosure(bs, s0);
        // compute decoration like at page 67
        FA<DBSState, BSTransition> decoratedSilentClosure = BFANetworkSupervisor.decoratedSilentClosure(silentClosure);
        assertEquals("",
                BFANetworkSupervisor.diagnosis(decoratedSilentClosure).get(decoratedSilentClosure.getInitialState()));
    }

    @Test
    public void itShouldComputeDecoratedSpaceOfClosures() {
        FA<FA<DBSState, BSTransition>, DSCTransition> space = BFANetworkSupervisor
                .decoratedSpaceOfClosures(behavioralSpaceFromPage38());
        assertEquals(7, space.getStates().size());
        assertEquals("0", space.getInitialState().getInitialState().getName());
        Set<String> acceptanceStatesOfSpace = space.getAcceptanceStates().stream()
                .map(s -> s.getInitialState().getName()).collect(Collectors.toSet());
        assertEquals(Set.of("0", "2"), acceptanceStatesOfSpace);
        assertEquals(12, space.getTransitions().size());
        Set<FA<DBSState, BSTransition>> decoratedClosuresContainingBSState1 = space.getNodes().stream()
                .filter(s -> s.getNodes().stream().anyMatch(d -> d.getBSState().getName().equals("1")))
                .collect(Collectors.toSet());
        assertEquals(4, decoratedClosuresContainingBSState1.size());

    }

    @Test
    public void itShouldComputeDiagnostician() {
        FA<FA<DBSState, BSTransition>, DSCTransition> space = BFANetworkSupervisor
                .decoratedSpaceOfClosures(behavioralSpaceFromPage38());
        Diagnostician d = BFANetworkSupervisor.diagnostician(space);
        assertEquals(7, d.getFa().getStates().size());
        FAState x2 = d.getFa().getNode("2").get();
        FAState x0 = d.getFa().getNode("0").get();
        assertEquals("|frf|fr|f", d.getDiagnosisOf(x2));
        assertEquals("", d.getDiagnosisOf(x0));
    }

    @Test
    public void itShouldComputeLinearDiagnosis() {
        FA<FA<DBSState, BSTransition>, DSCTransition> space = BFANetworkSupervisor
                .decoratedSpaceOfClosures(behavioralSpaceFromPage38());
        Diagnostician d = BFANetworkSupervisor.diagnostician(space);
        List<String> linObs = List.of("o3", "o2", "o3", "o2");
        String diagnosis = null;
        try {
            diagnosis = BFANetworkSupervisor.linearDiagnosis(d, linObs);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        assertEquals("(rf|fr(|frf|fr|f))", diagnosis);
    }

    /**
     * Check that the BFANetwork can be converted to Json, written to a file, loaded
     * back from the file, and converted again into an equivalent BFANetwork.
     */
    @Disabled
    @Test
    public void itShouldConvertFAtoJson() throws IOException {
        FileUtils fileUtils = new FileUtils("test");

        // save
        fileUtils.storeBFANetwork(bfaNetwork);

        // load
        BFANetwork netNew = fileUtils.loadBFANetwork();
        /*
         * String diagnosis = BFANetworkSupervisor.linearDiagnosis(
         * BFANetworkSupervisor.diagnostician(
         * BFANetworkSupervisor.decoratedSpaceOfClosures(BFANetworkSupervisor.
         * getBehavioralSpace(netNew))), List.of("o3", "o2", "o3", "o2"));
         */
        FA<BSState, BSTransition> bs = BFANetworkSupervisor.getBehavioralSpace(netNew);
        FA<FA<DBSState, BSTransition>, DSCTransition> space = BFANetworkSupervisor.decoratedSpaceOfClosures(bs);
        Diagnostician d = BFANetworkSupervisor.diagnostician(space);
        List<String> linObs = List.of("o3", "o2", "o3", "o2");
        String diagnosis = null;
        try {
            diagnosis = BFANetworkSupervisor.linearDiagnosis(d, linObs);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        System.out.println(diagnosis);
        // old BFA's states names
        Set<String> oldNames = bfaNetwork.getBFAs().stream().map(BFA::getName).collect(Collectors.toSet());

        // new FA's states names
        Set<String> newNames = netNew.getBFAs().stream().map(BFA::getName).collect(Collectors.toSet());
        assertEquals(oldNames, newNames);
    }
}