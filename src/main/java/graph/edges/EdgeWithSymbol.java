package graph.edges;

/**
 * This interface represents an edge with a symbol from an alphabet associated to it. Examples of
 * edges with symbol are FAs' Transitions or Network of BFAs' Links.
 *
 * @author Pietro Venturini
 */
public interface EdgeWithSymbol extends Edge {
    /**
     * @return the symbol associated with the edge
     */
    String getSymbol();

    /**
     * Set the symbol associated with the edge
     */
    void setSymbol(String symbol);
}
