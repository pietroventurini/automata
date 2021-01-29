import files.FileUtils;
import graph.fa.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private static final FAState s0 = new FAState("0");
    private static final FAState s1 = new FAState("1");
    private static final FAState s2 = new FAState("2");
    private static final FAState s3 = new FAState("3");
    private static final FAState s4 = new FAState("4");

    private static final FAState initialState = s0;
    private static final Set<FAState> acceptanceStates = Set.of(s4);

    private static final Transition t01 = new Transition("a");
    private static final Transition t02 = new Transition("a");
    private static final Transition t13 = new Transition("");
    private static final Transition t33 = new Transition("a");
    private static final Transition t34 = new Transition("c");
    private static final Transition t22 = new Transition("c");
    private static final Transition t23 = new Transition("b");

    FA<FAState, Transition> fa;
    FABuilder<FAState, Transition> faBuilder;

    @BeforeEach
    public void setUp() {
        faBuilder = new FABuilder<>();
    }

    /**
     * Build the FA from the example of page 9 of the project description
     */
    private FA<FAState, Transition> FAofPage9() {
        return faBuilder.name("FAOfPage9").putInitialState(s0).putAcceptanceStates(acceptanceStates)
                .putTransition(s0, s1, t01).putTransition(s0, s2, t02).putTransition(s1, s3, t13)
                .putTransition(s3, s3, t33).putTransition(s3, s4, t34).putTransition(s2, s2, t22)
                .putTransition(s2, s3, t23).build();
    }

    /**
     * Build a FA having only one state
     */
    private FA<FAState, Transition> FAWithOnlyOneState() {
        FAState theOnlyState = new FAState("theOnlyState");
        return faBuilder.putInitialState(theOnlyState).putAcceptanceState(theOnlyState).build();
    }

    /**
     * Build the FA from the example of page 21 of the project description
     */
    private FA<FAState, Transition> FAofPage21() {
        FAState s0 = new StateBuilder("0").build();
        FAState s1 = new StateBuilder("1").build();
        FAState s2 = new StateBuilder("2").build();
        FAState s3 = new StateBuilder("3").build();
        Transition t01 = new Transition("a");
        Transition t11 = new Transition("b");
        Transition t02 = new Transition("b");
        Transition t22 = new Transition("a");
        Transition t13 = new Transition("");
        Transition t23 = new Transition("b");
        return faBuilder.putInitialState(s0).putAcceptanceState(s1).putAcceptanceState(s2).putAcceptanceState(s3)
                .putTransition(s0, s1, t01).putTransition(s1, s1, t11).putTransition(s0, s2, t02)
                .putTransition(s2, s2, t22).putTransition(s1, s3, t13).putTransition(s2, s3, t23).build();
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
        assertThrows(IllegalStateException.class, () -> faBuilder.putAcceptanceState(s4).build());
    }

    /**
     * Construct a FA without any acceptance state and check that it throws an
     * IllegalStateException
     */
    @Test
    public void itShouldThrowExceptionIfThereIsntAnyAcceptanceState() {
        assertThrows(IllegalStateException.class, () -> faBuilder.putInitialState(s0).build());
    }

    /**
     * Try to construct a FA that contains isolated states and check that an
     * IllegalStateException is thrown
     */
    @Test
    public void itShouldThrowExceptionIfContainsIsolatedStates() {
        assertThrows(IllegalStateException.class,
                () -> faBuilder.putInitialState(new StateBuilder("initialIsolatedState").build())
                        .putAcceptanceState(new StateBuilder("anotherIsolatedState").build()).build());
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
        FA<FAState, Transition> fa = FAWithOnlyOneState();
        // add the self-loop
        fa.addEdge(fa.getInitialState(), fa.getInitialState(), new Transition("a"));
        String acceptedLanguage = AcceptedLanguage.reduceFAtoRegex(fa);
        assertEquals("((a)*)", acceptedLanguage);
    }

    /**
     * Check the example of page 21 applying the EspressioniRegolari described at
     * pages 17-20
     */
    @Test
    public void itShouldComputeAcceptedLanguagesRelativeToEachAcceptanceStateOfFaOfPage21() {
        FA<FAState, Transition> fa = FAofPage21();
        Set<String> acceptedLanguages = AcceptedLanguages.reduceFAtoMultipleRegex(fa);
        System.out.println(acceptedLanguages);
        Set<String> realAcceptedLanguages1 = Set.of("(a(b)*)", "((b(a)*b)|(a(b)*))", "(b(a)*)");
        Set<String> realAcceptedLanguages2 = Set.of("(a(b)*)", "((a(b)*)|(b(a)*b))", "(b(a)*)");
        assertTrue(
                acceptedLanguages.equals(realAcceptedLanguages1) || acceptedLanguages.equals(realAcceptedLanguages2));
    }

    /**
     * Check that the FA can be converted to Json, written to a file, loaded back
     * from the file, and converted again into a FA.
     */
    @Disabled
    @Test
    public void itShouldConvertFAtoJson() {
        FA<FAState, Transition> fa = FAofPage9();
        FileUtils fileUtils = new FileUtils("test");

        // save
        fileUtils.storeFA(fa);
        // load
        FA<FAState, Transition> faNew = fileUtils.loadFA(fa.getName());

        // old FA's states names
        Set<String> oldNames = fa.getStates().stream().map(FAState::getName).collect(Collectors.toSet());
        // new FA's states names
        Set<String> newNames = faNew.getStates().stream().map(FAState::getName).collect(Collectors.toSet());
        assertEquals(oldNames, newNames);
    }
}