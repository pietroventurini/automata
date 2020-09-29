import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

/**
 * This is the base class which is supposed to be extended by the builders of specific types of networks,
 * such as FA, BehavioralFA, BFANetwork...
 * @param <N> the type of the network's nodes
 * @param <E> the type of the network's edges
 */
public abstract class GraphBuilder<N extends Node, E extends Edge> {

    protected MutableNetwork<N, E> network;

    /**
     * Instantiate the underlying network with some default properties.
     */
    GraphBuilder() {
        network = NetworkBuilder.directed()
                                .allowsParallelEdges(true)
                                .build();
    }

    public GraphBuilder addEdge(N nodeU, N nodeV, E edge) {
        network.addEdge(nodeU, nodeV, edge);
        return this;
    }

    public abstract Graph<N,E> build();

}
