package graph.bfa;

import com.google.common.graph.MutableNetwork;
import graph.AbstractFA;
import graph.fa.State;

/**
 * This class represents a behavioral FA. It inherits all the methods of its superclass AbstractFA.
 *
 * TODO: implement class
 */
public class BFA extends AbstractFA<State, EventTransition> {

    private State initialState;
    private State currentState;

    public BFA(MutableNetwork<State, EventTransition> network, State initialState, State currentState) {
        super(network);
        this.initialState = initialState;
        this.currentState = currentState;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }


}
