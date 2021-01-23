package graph.bfa;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import graph.nodes.State;

import java.util.Set;

/**
 * This class is used to instantiate Behavioral FAs (BFAs), simplifying the construction process
 * of complex objects with different representations.
 *
 * @author Pietro Venturini
 */
public class BFABuilder {

    private MutableNetwork<State, EventTransition> network;
    private State initialState;
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

    public BFABuilder putStates(Set<State> states) {
        for (State s : states) {
            network.addNode(s);
        }
        return this;
    }

    public BFABuilder putInitialState(State nodeU) {
        network.addNode(nodeU);
        initialState = nodeU;
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
        BFA bfa = new BFA(name, network, initialState, initialState);
        BFAValidator.validate(bfa);
        return bfa;
    }



}
