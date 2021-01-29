package graph.BFAnetwork;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graphs;
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

    /**
     * Check if the BFANetwork is in its initial state
     * 
     * @return
     */
    public boolean isInitial() {
        return getNodes().stream().allMatch(BFA::isInitial) && getLinks().stream().allMatch(Link::isEmpty);
    }

    /**
     * Check if the BFANetwork is in a final state
     * 
     * @return
     */
    public boolean isFinal() {
        return getLinks().stream().allMatch(Link::isEmpty);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static BFANetwork copyOf(BFANetwork bfaNetwork) {
        return new BFANetwork(Graphs.copyOf(bfaNetwork.getNetwork()));
    }

    public void printDescription() {
        MutableNetwork<BFA, Link> network = this.getNetwork();
        System.out.println("\nList of bfas:");
        for (BFA bfa : this.getNodes()) {
            System.out.println("- " + bfa.getName());
        }
        System.out.println("\nList of transitions:");
        for (Link l : this.getEdges()) {
            EndpointPair<BFA> pair = network.incidentNodes(l);
            System.out.println("- " + pair.nodeU().getName() + " -> " + l.getName() + " ->  " + pair.nodeV().getName());
        }
        System.out.println("\n\n");
    }
}
