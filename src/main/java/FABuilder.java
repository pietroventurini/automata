import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

public class FABuilder implements GraphBuilder<State, Transition> {

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
        /* FIXME: if we want to validate a FA during construction, we should have
        *   methods addTransition and so on, into the builder, and validate before creation.
        *   Problem: if we do so, if we wanted to add a new transition to the existing FA,
        *   we would have to validate it one more time, so, maybe, is better to leave addTransition into the FA
        *   as it happens with Graph/Network of the Guava library
        */
        return new FA(network);
    }

}
