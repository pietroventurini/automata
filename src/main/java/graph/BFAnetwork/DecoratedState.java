package graph.BFAnetwork;

import graph.fa.State;
import graph.nodes.Node;

/**
 * Use decorator pattern
 */
public class DecoratedState implements Node {
    private State state;
    private String decoration;

    public DecoratedState(State state, String decoration) {
        this.state = state;
        this.decoration = decoration;
    }

    @Override
    public void setName(String name) {
        state.setName(name);
    }

    @Override
    public String getName() {
        return state.getName();
    }

    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }
}
