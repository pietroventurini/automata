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
public class FABuilder<S extends State, T extends Transition> {

    private MutableNetwork<S, T> network;

    /**
     * Instantiate the underlying network
     */
    public FABuilder() {
        network = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .build();
    }

    public FABuilder putState(S nodeU) {
        network.addNode(nodeU);
        return this;
    }

    public FABuilder putTransition(S nodeU, S nodeV, T transition) {
        network.addEdge(nodeU, nodeV, transition);
        return this;
    }


    public FA<S,T> build() {
        S initialState = retrieveInitialState();
        Set<S> finalStates = retrieveFinalStates();
        FA<S,T> fa = new FA(network, initialState, finalStates);

        FAValidator.validate(fa);

        return fa;
    }

    /**
     * @return the initial state of the FA
     * @throws NoSuchElementException if the iterable is empty
     * @throws IllegalArgumentException if the iterable contains multiple elements
     */
    private S retrieveInitialState() {
        return network.nodes()
                .stream()
                .filter(S::isInitial)
                .collect(MoreCollectors.onlyElement());
    }

    /**
     * @return the set of final states of the FA
     */
    private Set<S> retrieveFinalStates() {
        return network.nodes()
                .stream()
                .filter(S::isFinal)
                .collect(Collectors.toSet());
    }

}
