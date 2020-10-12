import graph.fa.Transition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the methods associated to the construction and transformation of transitions
 *
 * @author Pietro Venturini
 */
public class TransitionTest {

    @Test
    public void itShouldAddAnAlternativeSymbol() {
        Transition transition = new Transition("a");
        transition.addAlternativeSymbol("b");
        assertEquals("a|b", transition.getSymbol());
    }

}
