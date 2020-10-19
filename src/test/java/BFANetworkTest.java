import graph.BFAnetwork.BFANetwork;
import graph.BFAnetwork.BFANetworkBuilder;
import graph.BFAnetwork.BFANetworkSupervisor;
import graph.BFAnetwork.Link;
import graph.bfa.BFA;
import graph.bfa.BFABuilder;
import graph.bfa.EventTransition;
import graph.fa.State;
import graph.fa.StateBuilder;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BFANetworkTest {

    // nodes and transitions of BFA C2 from page 24
    private static final State s20 = new StateBuilder("20").isInitial(true).build();
    private static final State s21 = new StateBuilder("21").build();
    private static final EventTransition t2a = new EventTransition.Builder("t2a").inEvent("e2(L2)")
            .addOutEvent("e3(L3)").build();
    private static final EventTransition t2b = new EventTransition.Builder("t2b").addOutEvent("e3(L3)").build();

    // nodes and transitions of BFA C3 from page 24
    private static final State s30 = new StateBuilder("30").isInitial(true).build();
    private static final State s31 = new StateBuilder("31").build();
    private static final EventTransition t3a = new EventTransition.Builder("t3a").addOutEvent("e2(L2)").build();
    private static final EventTransition t3b = new EventTransition.Builder("t3b").inEvent("e3(L3)").build();
    private static final EventTransition t3c = new EventTransition.Builder("t3c").inEvent("e3(L3)").build();

    // links in the BFA Network's topology of page 26
    private static Link l2 = new Link("L2");
    private static Link l3 = new Link("L3");

    // bfas in the network
    private static BFA c2;
    private static BFA c3;

    /**
     * set the behavioral FA C2 from page 24
     */
    public BFA BFAc2FromPage24() {
        return new BFABuilder("C2").putTransition(s20, s21, t2a).putTransition(s21, s20, t2b).build();
    }

    /**
     * set the behavioral FA C3 from page 24
     */
    public BFA BFAc3FromPage24() {
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

    @Test
    public void t3aShouldBeEnabled() {
        BFANetwork bfaNetwork = BFANetworkFromPage26();
        Set<EventTransition> transitionsEnabledInC3 = BFANetworkSupervisor.getTransitionsEnabledInBfa(bfaNetwork, c3);
        assertTrue(transitionsEnabledInC3.contains(t3a));
    }

    @Test
    public void executeTransitionT3A() {
        BFANetwork bfaNetwork = BFANetworkFromPage26();
        BFANetworkSupervisor.executeTransition(bfaNetwork, c3, t3a);
        assertSame(c3.getCurrentState(), s31);
        assertSame(c2.getCurrentState(), s20);
        assertTrue(l2.getEvent().get().equals("e2(L2)"));
        assertTrue(l3.getEvent().isEmpty());
    }

    @Test
    public void checkIfTransitionT2AIsEnabledafterT3A() {
        BFANetwork bfaNetwork = BFANetworkFromPage26();
        BFANetworkSupervisor.executeTransition(bfaNetwork, c3, t3a);
        Set<EventTransition> transitionsEnabledInC2 = BFANetworkSupervisor.getTransitionsEnabledInBfa(bfaNetwork, c2);
        assertTrue(transitionsEnabledInC2.contains(t2a));

    }

    @Test
    public void executeTransitionT2AafterT3A() {
        BFANetwork bfaNetwork = BFANetworkFromPage26();
        BFANetworkSupervisor.executeTransition(bfaNetwork, c3, t3a);
        BFANetworkSupervisor.executeTransition(bfaNetwork, c2, t2a);
        assertSame(c3.getCurrentState(), s31);
        assertSame(c2.getCurrentState(), s21);
        assertTrue(l2.getEvent().isEmpty());
        assertTrue(l3.getEvent().get().equals("e3(L3)"));

    }

}