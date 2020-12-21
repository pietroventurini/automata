package graph.fa;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import graph.nodes.State;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to instantiate objects of type FA, simplifying the construction process
 * of complex objects with different representations.
 *
 * @author Pietro Venturini
 */
public class FABuilder<S extends State, T extends Transition> {

    private MutableNetwork<S,T> network;
    private S initialState;
    private Set<S> acceptanceStates = new HashSet<>();
    private Set<S> finalStates = new HashSet<>();
    private String name;

    /**
     * Instantiate the underlying network
     */
    public FABuilder() {
        network = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .build();
    }

    public FABuilder<S,T> name(String name) {
        this.name = name;
        return this;
    }

    public FABuilder<S,T> putState(S state) {
        network.addNode(state);
        return this;
    }

    public FABuilder<S,T> putInitialState(S state) {
        network.addNode(state);
        initialState = state;
        return this;
    }

    public FABuilder<S,T> putAcceptanceState(S state) {
        network.addNode(state);
        acceptanceStates.add(state);
        return this;
    }

    public FABuilder<S,T> putAcceptanceStates(Set<S> states) {
        for (S s : states) {
            network.addNode(s);
        }
        acceptanceStates.addAll(states);
        return this;
    }

    public FABuilder<S,T> putFinalState(S state) {
        network.addNode(state);
        finalStates.add(state);
        return this;
    }

    public FABuilder<S,T> putFinalStates(Set<S> states) {
        for (S s : states) {
            network.addNode(s);
        }
        finalStates.addAll(states);
        return this;
    }

    public FABuilder<S,T> putTransition(S nodeU, S nodeV, T transition) {
        network.addEdge(nodeU, nodeV, transition);
        return this;
    }


    public FA<S,T> build() {
        FA<S,T> fa = new FA<>(name, network, initialState, acceptanceStates, finalStates);
        FAValidator.validate(fa);
        return fa;
    }

}
