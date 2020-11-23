package graph;

import com.google.common.graph.MutableNetwork;
import graph.edges.Edge;
import graph.nodes.Node;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * An abstract structure that represents a graph (it can be a FA, a behavioral
 * FA, a network of BFA, a behavioral space, ...) Since the Guava Network is a
 * final class (we can't extend it), this is a kind of a container for that data
 * structure
 *
 * @param <N> Node parameter type
 * @param <E> Edge parameter type
 *
 * @author Pietro Venturini
 */
public abstract class Graph<N extends Node, E extends Edge> {

    private MutableNetwork<N, E> network;

    public Graph(MutableNetwork<N, E> network) {
        this.network = network;
    }

    public Set<E> getEdges() {
        return network.edges();
    }

    public boolean addEdge(N nodeU, N nodeV, E edge) {
        return network.addEdge(nodeU, nodeV, edge);
    }

    public Set<N> getNodes() {
        return network.nodes();
    }

    /**
     * @return the underlying network
     */
    public MutableNetwork<N, E> getNetwork() {
        return network;
    }

    /**
     * Return node having name equal to {@code name}
     */
    public Optional<N> getNode(String name) {
        return network.nodes().stream().filter(n -> n.getName().equals(name)).findAny();
    }

    public Set<N> successors(N n) {
        return network.successors(n);
    }

    @Override
    public String toString() {
        return "network='" + network + '\'';
    }

}
