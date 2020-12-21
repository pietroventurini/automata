import graph.fa.StateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the instantiation of states
 *
 * @author Pietro Venturini
 */
public class StateBuilderTest {


    @Test
    public void itShouldBuildState() {
        assertEquals("test", new StateBuilder("test").build().getName());
    }

}