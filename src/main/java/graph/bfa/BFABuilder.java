package graph.bfa;

import com.google.common.collect.MoreCollectors;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import graph.fa.*;

import java.util.NoSuchElementException;

/**
 * This class is used to instantiate Behavioral FAs (BFAs), simplifying the construction process
 * of complex objects with different representations.
 *
 * @author Pietro Venturini
 */
public class BFABuilder {

    private MutableNetwork<State, EventTransition> network;
    private String name;

    /**
     * Set the name of the BFA and instantiate the underlying network
     */
    public BFABuilder(String name) {
        this.name = name;
        network = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .build();
    }

    public BFABuilder name(String name) {
        this.name = name;
        return this;
    }


    public BFABuilder putState(State nodeU) {
        network.addNode(nodeU);
        return this;
    }

    /**
     * Add a transition to the underlying network of the BFA
     * @param nodeU the source State
     * @param nodeV the destination State
     * @param transition the (event-)transition itself
     */
    public BFABuilder putTransition(State nodeU, State nodeV, EventTransition transition) {
        network.addEdge(nodeU, nodeV, transition);
        return this;
    }

    public BFA build() {
        State initialState = retrieveInitialState();
        BFA bfa = new BFA(name, network, initialState, initialState);

        BFAValidator.validate(bfa);

        return bfa;
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


}
