package graph.BFAnetwork;

import com.google.common.graph.MutableNetwork;
import graph.Graph;
import graph.bfa.BFA;

import java.util.Set;

/**
 * This class represents a network consisting of BFAs and Links connecting them.
 *
 * @author Giacomo Bontempi
 */
public class BFANetwork extends Graph<BFA, Link> {

    public BFANetwork(MutableNetwork<BFA, Link> network) {
        super(network);
    }

    /**
     * This is an alias of Graph.getEdges()
     * 
     * @return the set of links in the underlying network
     */
    public Set<Link> getLinks() {
        return super.getEdges();
    }

    /**
     * This is an alias of Graph.getNodes()
     * 
     * @return the set of BFAs in the underlying network
     */
    public Set<BFA> getBFAs() {
        return super.getNodes();
    }

}
