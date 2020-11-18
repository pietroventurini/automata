import graph.BFAnetwork.*;
import graph.bfa.BFA;
import graph.bfa.BFABuilder;
import graph.bfa.EventTransition;
import graph.fa.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.graph.EndpointPair;

import static org.junit.jupiter.api.Assertions.*;

public class BFANetworkTest {

    // nodes and transitions of BFA C2 from page 24
    private static final State s20 = new StateBuilder("20").isInitial(true).build();
    private static final State s21 = new StateBuilder("21").build();
    private static final EventTransition t2a = new EventTransition.Builder("t2a").inEvent("e2(L2)")
            .addOutEvent("e3(L3)").observabilityLabel("o2").build();
    private static final EventTransition t2b = new EventTransition.Builder("t2b").addOutEvent("e3(L3)")
            .relevanceLabel("r").build();

    // nodes and transitions of BFA C3 from page 24
    private static final State s30 = new StateBuilder("30").isInitial(true).build();
    private static final State s31 = new StateBuilder("31").build();
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
    private static BFA BFAc2FromPage24() {
        return new BFABuilder("C2").putTransition(s20, s21, t2a).putTransition(s21, s20, t2b).build();
    }

    /**
     * @return the behavioral FA C3 from page 24
     */
    private static BFA BFAc3FromPage24() {
        return new BFABuilder("C3").putTransition(s30, s31, t3a).putTransition(s31, s30, t3b)
                .putTransition(s31, s31, t3c).build();
    }

    /**
     * @return the behavioral FA Network from page 26
     */
    public BFANetwork BFANetworkFromPage26() {
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
        Map<BFA, State> bfas = new HashMap<>();
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
        assertEquals(4, space.getStates().stream().filter(s -> s.isFinal()).collect(Collectors.toSet()).size());

        // there should be 18 transitions
        assertEquals(18, space.getTransitions().size());
        /*
         * for (BSState state : space.getStates()) {
         * System.out.println(state.description()); }
         * 
         * for (BSTransition transition : space.getTransitions()) {
         * EndpointPair<BSState> pair = space.getNetwork().incidentNodes(transition);
         * System.out.println( pair.nodeU().description() + "-> " +
         * transition.getSymbol() + "-> " + pair.nodeV().description()); }
         */
    }

    @Test
    public void computeBehavioralSpaceOfLinearObservation() {
        ArrayList<String> linearObservation = new ArrayList<>();
        linearObservation.add("o3");
        linearObservation.add("o2");
        FA<LOBSState, BSTransition> space = BFANetworkSupervisor.getBehavioralSpaceForLinearObservation(bfaNetwork,
                linearObservation);

        // there should be 9 states
        assertEquals(9, space.getStates().size());

        // there should be 4 final states
        assertEquals(4, space.getStates().stream().filter(s -> s.isFinal()).collect(Collectors.toSet()).size());

        // there should be 18 transitions
        assertEquals(8, space.getTransitions().size());

        LOBSState toDelete = space.getStates().stream().filter(s -> s.getName().equals(" 30 20 eps e3(L3)"))
                .collect(Collectors.toSet()).iterator().next();
        space.getNetwork().removeNode(toDelete);

        Set<LOBSState> finalStates = space.getFinalStates();
        for (LOBSState state : finalStates) {
            state.isAcceptance(true);
        }

        Set<String> acceptedLanguages = AcceptedLanguages.reduceFAtoMultipleRegex(space);

        Set<String> realAcceptedLanguages = Set.of("", "f", "fr", "frf");

        assertEquals(acceptedLanguages, realAcceptedLanguages);

    }

    // TO-DO (this test is non fully implemented, it is just an idea for silent
    // closures)
    @Test
    public void computeSilentClosure() {
        ArrayList<String> linearObservation = new ArrayList<>();
        BFANetworkSupervisor.executeTransition(bfaNetwork, c3, t3a);
        BFANetworkSupervisor.executeTransition(bfaNetwork, c2, t2a);
        FA<LOBSState, BSTransition> space = BFANetworkSupervisor.getBehavioralSpaceForLinearObservation(bfaNetwork,
                linearObservation);

        LOBSState toDelete = space.getStates().stream().filter(s -> s.getName().equals(" 30 20 eps e3(L3)"))
                .collect(Collectors.toSet()).iterator().next();
        toDelete.isAcceptance(true);
        // space.getNetwork().removeNode(toDelete);

        Set<LOBSState> finalStates = space.getFinalStates();
        for (LOBSState state : finalStates) {
            state.isAcceptance(true);
        }

        String acceptedLanguage = AcceptedLanguage.reduceFAtoRegex(space);

        System.out.println(acceptedLanguage);
        assertTrue(true);

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
     * s0 s1 --> s2 s2 --> s1
     *
     * where: initial state: s0 final states: {s0}
     *
     * The expected FA after pruning should contain only s0.
     */
    @Test
    public void itShouldPruneFAWithLoopOfNonFinalStates() {
        State s0 = new StateBuilder("s0").isInitial(true).isFinal(true).build();
        State s1 = new State("s1");
        State s2 = new State("s2");
        FA<State, Transition> space = new FABuilder<>().putState(s0).putTransition(s1, s2, new Transition(""))
                .putTransition(s2, s1, new Transition("")).build();
        BFANetworkSupervisor.pruneFA(space);
        assertTrue(space.getStates().contains(s0), "The BS after pruning should contain s0");
        assertFalse(space.getStates().contains(s1), "The BS after pruning should not contain s1");
        assertFalse(space.getStates().contains(s2), "The BS after pruning should not contain s2");
    }

}
