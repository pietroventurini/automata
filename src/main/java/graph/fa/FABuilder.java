package graph.fa;

import com.google.common.collect.MoreCollectors;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is used to instantiate objects of type FA, simplifying the construction process
 * of complex objects with different representations.
 *
 * @author Pietro Venturini
 */
public class FABuilder {

    private MutableNetwork<State, Transition> network;

    /**
     * Invokes the constructor of the superclass, which instantiates the underlying network
     */
    public FABuilder() {
        network = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .build();
    }

    public FABuilder putState(State nodeU) {
        network.addNode(nodeU);
        return this;
    }

    public FABuilder putTransition(State nodeU, State nodeV, Transition transition) {
        network.addEdge(nodeU, nodeV, transition);
        return this;
    }


    public FA build() {
        State initialState = retrieveInitialState();
        Set<State> finalStates = retrieveFinalStates();
        FA fa = new FA(network, initialState, finalStates);

        FAValidator.validate(fa);

        return fa;
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

}
