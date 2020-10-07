import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TransitionTest {

    @Test
    public void itShouldAddAnAlternativeSymbol() {
        Transition transition = new Transition("a");
        transition.addAlternativeSymbol("b");
        assertEquals("a|b", transition.getSymbol());
    }

}
