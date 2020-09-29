import com.google.common.graph.MutableNetwork;

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
}
