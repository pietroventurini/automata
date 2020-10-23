package graph.BFAnetwork;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import graph.bfa.BFA;

/**
 * This class is used to instantiate Behavioral FAs (BFAs), simplifying the
 * construction process of complex objects with different representations
 *
 * @author Giacomo Bontempi
 */
public class BFANetworkBuilder {

    private MutableNetwork<BFA, Link> network;

    /**
     * Instantiate the underlying network
     */
    public BFANetworkBuilder() {
        network = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .build();
    }

    public BFANetworkBuilder putBFA(BFA nodeU) {
        network.addNode(nodeU);
        return this;
    }

    /**
     * Add a link to the underlying network
     * 
     * @param nodeU the source BFA
     * @param nodeV the destination BFA
     * @param link  the link
     */
    public BFANetworkBuilder putLink(BFA nodeU, BFA nodeV, Link link) {
        network.addEdge(nodeU, nodeV, link);
        return this;
    }

    public BFANetwork build() {
        BFANetwork bfaNetwork = new BFANetwork(network);

        return bfaNetwork;
    }

}
