package graph.nodes;

/**
 * It represents a node of a graph (it can be a State, a FA, a Behavioral FA, ...)
 *
 * @author Pietro Venturini
 */
public interface State {

    void setName(String name);

    String getName();

}
