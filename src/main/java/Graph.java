import com.google.common.graph.MutableNetwork;

import java.util.Set;

/**
 * An abstract structure that represents a graph (it can be a FA, a behavioral FA, a network of BFA,
 * a behavioral space, ...)
 * Since the Guava Network is a final class (we can't extend it), this is a kind of a container
 * for that data structure
 *
 * @param <N> Node parameter type
 * @param <E> Edge parameter type
 */
public abstract class Graph<N extends Node, E extends Edge> {

    protected MutableNetwork<N, E> network;

    public Graph(MutableNetwork<N, E> network) {
        this.network = network;
    }

    Set<E> getEdges() {
        return network.edges();
    }

    boolean addEdge(N nodeU, N nodeV, E edge) {
        return network.addEdge(nodeU, nodeV, edge);
    }

    /**
     * Validate the internal state of the graph
     */
    abstract boolean validate();
}
