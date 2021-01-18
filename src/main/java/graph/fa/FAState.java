package graph.fa;

import graph.nodes.State;

/**
 * This class represents a state of a FA. A state is a node of the underlying
 * graph, with a unique name.
 *
 * @author Pietro Venturini
 */
public class FAState implements State {

    private String name;

    public FAState(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "name='" + name + "'";
    }
}
