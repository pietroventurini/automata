import com.google.common.collect.MoreCollectors;
import com.google.common.graph.MutableNetwork;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represent a Finite Automata (or Finite State Machine), which consists of a finite, non-empty
 * set of states, where one is the initial state, and a finite set of transitions.
 * We distinguish acceptance states from those which aren't.
 */
public class FA extends Graph<State,Transition> {

    private State initialState;
    private Set<State> finalStates;

    /**
     * Just for testing purposes, later on we will build it from a file containing
     * states and transitions
     * @param network the underlying network of the FA
     */
    public FA(MutableNetwork<State,Transition> network) {
        super(network);
        this.initialState = retrieveInitialState();
        this.finalStates = retrieveFinalStates();
    }

    /**
     * Just for testing purposes, later on we will build it from a file containing
     * states and transitions
     * @param network the underlying network of the FA
     * @param initialState the initial state of the FA
     * @param finalStates the set (eventually empty) of final states
     */
    public FA(MutableNetwork<State,Transition> network, State initialState, Set<State> finalStates) {
        super(network);
        this.initialState = initialState;
        this.finalStates = finalStates;
    }

    /**
     * @return the initial state of the FA
     * @throws NoSuchElementException if the iterable is empty
     * @throws IllegalArgumentException if the iterable contains multiple elements
     */
    private State retrieveInitialState() {
        return network.nodes()
                .stream()
                .collect(MoreCollectors.onlyElement());
    }

    /**
     * @return the set of final states of the FA
     */
    private Set<State> retrieveFinalStates() {
        return network.nodes()
                .stream()
                .filter(State::isFinal)
                .collect(Collectors.toSet());
    }

    /**
     * @return the underlying network
     */
    public MutableNetwork<State, Transition> getNetwork() {
        return network;
    }

    public State getInitialState() {
        return initialState;
    }

    public Set<State> getFinalStates() {
        return finalStates;
    }
}
