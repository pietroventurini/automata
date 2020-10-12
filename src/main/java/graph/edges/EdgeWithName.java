package graph.edges;

/**
 * This interface represents an edge with a name. Examples of
 * graph.edges with names are Behavioral FAs' Transitions, Network of BFAs' Links, Behavioral Spaces' transitions.
 *
 * @author Pietro Venturini
 */
public interface EdgeWithName extends Edge {
    /**
     * @return the name of the edge
     */
    String getName();

    /**
     * Set the name of the edge
     */
    void setName(String name);
}
