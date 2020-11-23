import graph.fa.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test different FAs' instantiations and the utility classes that implement the
 * logic to retrieve the language accepted by a FA or the languages accepted by
 * each of its acceptance states.
 *
 * @author Pietro Venturini
 */
public class FATest {

    // define states and transitions of FA from page 9 of the project description
    private static final State s0 = new StateBuilder("0").isInitial(true).build();
    private static final State s1 = new StateBuilder("1").build();
    private static final State s2 = new StateBuilder("2").build();
    private static final State s3 = new StateBuilder("3").build();
    private static final State s4 = new StateBuilder("4").isFinal(true).isAcceptance(true).build();

    private static final State initialState = s0;
    private static final Set<State> acceptanceStates = Set.of(s4);

    private static final Transition t01 = new Transition("a");
    private static final Transition t02 = new Transition("a");
    private static final Transition t13 = new Transition("");
    private static final Transition t33 = new Transition("a");
    private static final Transition t34 = new Transition("c");
    private static final Transition t22 = new Transition("c");
    private static final Transition t23 = new Transition("b");

    FA<State, Transition> fa;
    FABuilder<State, Transition> faBuilder;

    @BeforeEach
    public void setUp() {
        faBuilder = new FABuilder<>();
    }

    /**
     * Build the FA from the example of page 9 of the project description
     */
    private FA<State, Transition> FAofPage9() {
        return faBuilder.putTransition(s0, s1, t01).putTransition(s0, s2, t02).putTransition(s1, s3, t13)
                .putTransition(s3, s3, t33).putTransition(s3, s4, t34).putTransition(s2, s2, t22)
                .putTransition(s2, s3, t23).build();
    }

    /**
     * Build a FA having only one state
     */
    private FA<State, Transition> FAWithOnlyOneState() {
        return faBuilder
                .putState(new StateBuilder("theOnlyState").isInitial(true).isFinal(true).isAcceptance(true).build())
                .build();
    }

    /**
     * Build the FA from the example of page 21 of the project description
     */
    private FA<State, Transition> FAofPage21() {
        State s0 = new StateBuilder("0").isInitial(true).build();
        State s1 = new StateBuilder("1").isFinal(true).isAcceptance(true).build();
        State s2 = new StateBuilder("2").isFinal(true).isAcceptance(true).build();
        State s3 = new StateBuilder("3").isFinal(true).isAcceptance(true).build();
        Transition t01 = new Transition("a");
        Transition t11 = new Transition("b");
        Transition t02 = new Transition("b");
        Transition t22 = new Transition("a");
        Transition t13 = new Transition("");
        Transition t23 = new Transition("b");
        return faBuilder.putState(s0).putState(s1).putState(s2).putState(s3).putTransition(s0, s1, t01)
                .putTransition(s1, s1, t11).putTransition(s0, s2, t02).putTransition(s2, s2, t22)
                .putTransition(s1, s3, t13).putTransition(s2, s3, t23).build();
    }

    /**
     * Check if FA of page 9 is built correctly
     */
    @Test
    public void itShouldBuildFA() {
        fa = FAofPage9();
        assertSame(fa.getInitialState(), initialState);
        assertEquals(fa.getAcceptanceStates(), acceptanceStates);
    }

    /**
     * Construct a FA without an initial state and check that it throws a
     * NoSuchElementException
     */
    @Test
    public void itShouldThrowExceptionIfInitialStateIsMissing() {
        assertThrows(NoSuchElementException.class, () -> faBuilder.putState(s4).build());
    }

    /**
     * Construct a FA without any acceptance state and check that it throws an
     * IllegalStateException
     */
    @Test
    public void itShouldThrowExceptionIfThereIsntAnyAcceptanceState() {
        assertThrows(IllegalStateException.class, () -> faBuilder.putState(s0).build());
    }

    /**
     * Construct a FA with more than one initial state and check that it throws an
     * IllegalArgumentException
     */
    @Test
    public void itShouldThrowExceptionIfMoreThanOneInitialStateWhenBuilding() {
        assertThrows(IllegalArgumentException.class,
                () -> faBuilder.putState(new StateBuilder("anInitialState").isInitial(true).build())
                        .putState(new StateBuilder("anotherInitialState").isInitial(true).isFinal(true).build())
                        .build());
    }

    /**
     * Try to construct a FA that contains isolated states and check that an
     * IllegalStateException is thrown
     */
    @Test
    public void itShouldThrowExceptionIfContainsIsolatedStates() {
        assertThrows(IllegalStateException.class,
                () -> faBuilder.putState(new StateBuilder("initialIsolatedState").isInitial(true).build())
                        .putState(new StateBuilder("anotherIsolatedState").isFinal(true).build()).build());
    }

    /**
     * Check that if the set of states contains isolated states only if the set of
     * states is a singleton
     */
    @Test
    public void itShouldAllowIsolatedStatesIfThereIsOnlyOneState() {
        fa = FAWithOnlyOneState();
        assertTrue(FAValidator.validate(fa));
    }

    /**
     * Check the example of 13 computing the accepted language of the FA of page 9
     * Note: since there can be equivalent languages, it is quite difficult to test
     * whether the accepted language is correct. Furthermore, since we elaborate
     * transitions working on Sets (unordered collection), order of regex's elements
     * can change at every execution
     */
    @Test
    public void itShouldComputeLanguageAcceptedFromFA() {
        String acceptedLanguage = AcceptedLanguage.reduceFAtoRegex(FAofPage9());
        // check equivalent languages
        assertTrue(acceptedLanguage.equals("aa*c|ac*ba*c") || acceptedLanguage.equals("a(a*|c*ba*)c")
                || acceptedLanguage.equals("a(c*b)?a*c") || acceptedLanguage.equals("((a|(a(c)*b))(a)*c)")
                || acceptedLanguage.equals("(((a(c)*b)|a)(a)*c)"));
    }

    /**
     * Check the degenerate case of a FA with only one state without a self-loop.
     */
    @Test
    public void itShouldComputeAcceptedLanguageOfSingleState() {
        String acceptedLanguage = AcceptedLanguage.reduceFAtoRegex(FAWithOnlyOneState());
        assertEquals("", acceptedLanguage);
    }

    /**
     * Check the degenerate case of a FA with only one state with a self-loop.
     */
    @Test
    public void itShouldComputeAcceptedLanguageOfSingleStateWithSelfLoop() {
        FA<State, Transition> fa = FAWithOnlyOneState();
        // add the self-loop
        fa.addEdge(fa.getInitialState(), fa.getInitialState(), new Transition("a"));
        String acceptedLanguage = AcceptedLanguage.reduceFAtoRegex(fa);
        assertEquals("((a)*)", acceptedLanguage);
    }

    /**
     * Check the example of page 21 applying the EspressioniRegolari described at
     * pages 17-20
     *
     */
    @Test
    public void itShouldComputeAcceptedLanguagesRelativeToEachAcceptanceStateOfFaOfPage21() {
        FA<State, Transition> fa = FAofPage21();
        Set<String> acceptedLanguages = AcceptedLanguages.reduceFAtoMultipleRegex(fa);
        Set<String> realAcceptedLanguages = Set.of("(a(b)*)", "((b(a)*b)|(a(b)*))", "(b(a)*)");
        assertEquals(acceptedLanguages, realAcceptedLanguages);
    }

}