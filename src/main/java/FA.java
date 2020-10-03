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
     * @throws NoSuchElementException if the iterable is empty
     * @throws IllegalArgumentException if the iterable contains multiple elements
     */
    public FA(MutableNetwork<State,Transition> network) {
        super(network);
        this.initialState = retrieveInitialState();
        this.finalStates = retrieveFinalStates();
        validate();
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
        validate();
    }

    /**
     * @return the initial state of the FA
     * @throws NoSuchElementException if the iterable is empty
     * @throws IllegalArgumentException if the iterable contains multiple elements
     */
    private State retrieveInitialState() {
        return network.nodes()
                .stream()
                .filter(State::isInitial)
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
        return super.network;
    }

    public State getInitialState() {
        return initialState;
    }

    public Set<State> getFinalStates() {
        return finalStates;
    }

    /**
     * Since edges of a FA are called transitions, this is a facade to Graph.getEdges()
     * @return the set of transitions of the FA
     */
    public Set<Transition> getTransitions() {
        return getEdges();
    }

    private Set<State> getIsolatedStates() {
        return network.nodes()
                .stream()
                .filter(n -> network.degree(n) == 0)
                .collect(Collectors.toSet());
    }

    @Override
    boolean validate() {
        return hasOnlyOneInitialState() && thereAreNoIsolatedStates();
    }

    private boolean hasOnlyOneInitialState() {
        return retrieveInitialState() == initialState;
    }

    /**
     * Check that, unless the set of states contains only one state, then there are no isolated states
     */
    private boolean thereAreNoIsolatedStates() {
        if (network.nodes().size() > 1)
            if (getIsolatedStates().size() > 0)
                throw new IllegalStateException();
        return true;
    }

    /**
     * Reduce the FA to an equivalent regular expression describing the language accepted from the FA
     * by applying the algorithm RegularExpression(Nin) described at page 11 of the project description.
     * @return the accepted language
     */
    public String regularExpression() {
        return AcceptedLanguage.reduceFAtoRegex(this);
    }

}
