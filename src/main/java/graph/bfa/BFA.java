package graph.bfa;

import com.google.common.graph.MutableNetwork;
import graph.AbstractFA;
import graph.fa.State;

import java.util.Objects;

/**
 * This class represents a behavioral FA (BFA). It inherits all the methods of
 * its superclass AbstractFA. A BFA has a name and doesn't have final states.
 * The transitions have input and output events associated to them.
 *
 * @author Pietro Venturini
 */
public class BFA extends AbstractFA<State, EventTransition> {

    private State currentState;

    /**
     * Constructor of a Behavioral Finite Automata
     *
     * @param name         the name of the BFA
     * @param network      the underlying network of the BFA
     * @param initialState the initial state of the BFA
     * @param currentState the current state of the BFA
     */
    BFA(String name, MutableNetwork<State, EventTransition> network, State initialState, State currentState) {
        super(name, network, initialState);
        this.currentState = currentState;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

}
