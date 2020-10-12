import graph.fa.State;
import graph.fa.StateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the instantiation of states
 *
 * @author Pietro Venturini
 */
public class StateBuilderTest {

    StateBuilder sb;

    @BeforeEach
    public void setUp() throws Exception {
        sb = new StateBuilder("test");
    }

    @Test
    public void itShouldBuildInitialState() {
        assertTrue(sb.isInitial(true).build().isInitial());
    }

    @Test
    public void itShouldBuildNoninitialState() {
        assertFalse(sb.isInitial(false).build().isInitial());
    }

    @Test
    public void itShouldBuildFinalState() {
        assertTrue(sb.isFinal(true).build().isFinal());
    }

    @Test
    public void itShouldBuildNonfinalState() {
        assertFalse(sb.isFinal(false).build().isFinal());
    }

    @Test
    public void itShouldBuildFinalAndInitialState() {
        State state = sb.isFinal(true).isInitial(true).build();
        assertTrue(state.isFinal() && state.isInitial());
    }

    @Test
    public void itShouldConvertInitialStateToNoninitial() {
        State state = sb.isInitial(true).build();
        state.isInitial(false);
        assertFalse(state.isInitial());
    }
}