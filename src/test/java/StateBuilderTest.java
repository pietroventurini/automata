import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StateBuilderTest {

    StateBuilder sb;

    @Before
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
}