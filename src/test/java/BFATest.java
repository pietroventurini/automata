import graph.bfa.BFA;
import graph.bfa.BFABuilder;
import graph.bfa.EventTransition;
import graph.fa.FAState;
import graph.fa.StateBuilder;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BFATest {

    // nodes and transitions of BFA C2 from page 24
    private static final FAState s20 = new StateBuilder("20").build();
    private static final FAState s21 = new StateBuilder("21").build();
    private static final EventTransition t2a = new EventTransition.Builder("t2a").inEvent("e2(L2)").addOutEvent("e3(L3)").build();
    private static final EventTransition t2b = new EventTransition.Builder("t2b").addOutEvent("e3(L3)").build();

    // nodes and transitions of BFA C3 from page 24
    private static final FAState s30 = new StateBuilder("30").build();
    private static final FAState s31 = new StateBuilder("31").build();
    private static final EventTransition t3a = new EventTransition.Builder("t3a").addOutEvent("e2(L2)").build();
    private static final EventTransition t3b = new EventTransition.Builder("t3b").inEvent("e3(L3)").build();
    private static final EventTransition t3c = new EventTransition.Builder("t3c").inEvent("e3(L3)").build();


    /**
     * @return the behavioral FA C2 from page 24
     */
    private static BFA BFAc2FromPage24() {
        return new BFABuilder("C2")
                .putInitialState(s20)
                .putTransition(s20, s21, t2a)
                .putTransition(s21, s20, t2b)
                .build();
    }

    /**
     * @return the behavioral FA C3 from page 24
     */
    private static BFA BFAc3FromPage24() {
        return new BFABuilder("C3")
                .putInitialState(s30)
                .putTransition(s30, s31, t3a)
                .putTransition(s31, s30, t3b)
                .putTransition(s31, s31, t3c)
                .build();
    }

    @Test
    public void itThrowExceptionIfInitialStateIsMissing() {
        assertThrows(IllegalStateException.class, () -> new BFABuilder("")
                .putState(new StateBuilder("aNonInitialState").build())
                .build());
    }

    /**
     * States' names must be unique in a BFA, so check that an IllegalStateException
     * is thrown if we try to build a BFA having states with duplicate names
     */
    @Test
    public void itShouldThrowExceptionIfThereAreStatesWithSameName() {
        FAState s1 = new StateBuilder("sameName").build();
        FAState s2 = new StateBuilder("sameName").build();
        assertThrows(IllegalStateException.class, () -> new BFABuilder("BFAWithHomonymsStates")
                .putInitialState(s1)
                .putTransition(s1, s2, t2a)
                .build());
    }

    /**
     * Transitions' names must be unique in a BFA, so check that an IllegalStateException
     * is thrown if we try to build a BFA having transitions with duplicate names
     */
    @Test
    public void itShouldThrowExceptionIfThereAreTransitionsWithSameName() {
        EventTransition t1 = new EventTransition.Builder("sameName").build();
        EventTransition t2 = new EventTransition.Builder("sameName").build();

        assertThrows(IllegalStateException.class, () -> new BFABuilder("BFAWithHomonymsTransitions")
                .putInitialState(s20)
                .putTransition(s20, s21, t1)
                .putTransition(s21, s20, t2)
                .build());
    }
}
