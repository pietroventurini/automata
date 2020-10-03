import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class FATest {

    // define states and transitions of FA from page 9 of the project description
    private static final State s0 = new StateBuilder("0").isInitial(true).build();
    private static final State s1 = new StateBuilder("1").build();
    private static final State s2 = new StateBuilder("2").build();
    private static final State s3 = new StateBuilder("3").build();
    private static final State s4 = new StateBuilder("4").isFinal(true).build();

    private static final State initialState = s0;
    private static final Set<State> finalStates = Set.of(s4);

    private static final Transition t01 = new Transition("a");
    private static final Transition t02 = new Transition("a");
    private static final Transition t13 = new Transition("");
    private static final Transition t33 = new Transition("a");
    private static final Transition t34 = new Transition("c");
    private static final Transition t22 = new Transition("c");
    private static final Transition t23 = new Transition("b");

    FA fa;
    FABuilder faBuilder;

    @BeforeEach
    public void setUp() {
        faBuilder = new FABuilder();
    }

    /**
     * Check if FA of page 9 is built correctly
     */
    @Test
    public void itShouldBuildFA() {
        fa = faBuilder.putTransition(s0, s1, t01)
                .putTransition(s0, s2, t02)
                .putTransition(s1, s3, t13)
                .putTransition(s3, s3, t33)
                .putTransition(s3, s4, t34)
                .putTransition(s2, s2, t22)
                .putTransition(s2, s3, t23)
                .build();
        assertSame(fa.getInitialState(), initialState);
        assertEquals(fa.getFinalStates(), finalStates);
    }

    /**
     * Construct a FA without an initial state and check that it throws a NoSuchElementException
     */
    @Test
    public void itShouldThrowExceptionIfInitialStateIsMissingWhenBuilding() {
        assertThrows(NoSuchElementException.class, () -> faBuilder.putState(s1).build());
    }

    /**
     * Construct a FA with more than one initial state and check that it throws an IllegalArgumentException
     */
    @Test
    public void itShouldThrowExceptionIfMoreThanOneInitialStateWhenBuilding() {
        assertThrows(IllegalArgumentException.class,
                    () -> faBuilder.putState(new StateBuilder("anInitialState").isInitial(true).build())
                                .putState(new StateBuilder("anotherInitialState").isInitial(true).build())
                                .build()
        );

    }

    /**
     * Try to construct a FA that contains isolated states and check that an IllegalStateException is thrown
     */
    @Test
    public void itShouldThrowExceptionIfContainsIsolatedStates() {
        assertThrows(IllegalStateException.class, () -> {
            faBuilder.putState(new StateBuilder("initialIsolatedState").isInitial(true).build())
                    .putState(new StateBuilder("anotherIsolatedState").build())
                    .build();
        });
    }

    /**
     * Check that if the set of states contains isolated states only if the set of states is a singleton
     */
    @Test
    public void itShouldAllowIsolatedStateIfThereIsOnlyOneState() {
        fa = faBuilder.putState(new StateBuilder("theOnlyState").isInitial(true).build())
                .build();
        assertTrue(fa.validate());
    }

    @Test
    public void itShouldComputeAcceptedLanguageRelativeToASingleAcceptanceState() {
        //TODO: implement EspressioneRegolare(N_in)
        fail("Test not yet implemented");
    }

    @Test
    public void itShouldComputeAcceptedLanguageRelativeToAllAcceptanceStates() {
        //TODO: implement EspressioniRegolari(N_in)
        fail("Test not yet implemented");
    }
}