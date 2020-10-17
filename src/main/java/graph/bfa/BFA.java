package graph.bfa;

import com.google.common.graph.MutableNetwork;
import graph.AbstractFA;
import graph.fa.State;
import graph.nodes.Node;

/**
 * This class represents a behavioral FA (BFA). It inherits all the methods of its superclass AbstractFA.
 * A BFA has a name and doesn't have final states. The transitions have input and output events associated to them.
 */
public class BFA extends AbstractFA<State, EventTransition> implements Node {

    private String name;
    private State currentState;

    BFA(String name, MutableNetwork<State, EventTransition> network, State initialState, State currentState) {
        super(network, initialState);
        this.name = name;
        this.currentState = currentState;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public State getCurrentState() {
        return currentState;
    }

    void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

}
