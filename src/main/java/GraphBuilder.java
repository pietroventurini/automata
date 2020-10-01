/**
 * This is the base interface which is supposed to be implemented by the builders of specific types of networks,
 * such as FA, BehavioralFA, BFANetwork...
 * @param <N> the type of the network's nodes
 * @param <E> the type of the network's edges
 */
public interface GraphBuilder<N extends Node, E extends Edge> {

    Graph<N,E> build();

}
