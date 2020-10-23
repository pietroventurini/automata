package graph.BFAnetwork;

import graph.bfa.BFA;
import graph.fa.State;
import graph.fa.StateType;

import java.util.EnumSet;
import java.util.Set;

/**
 * BSState is the State of a Behavioral Space, which has a more complex structure
 * than State of a FA. BSState represents a particular state (snapshot)
 * of a BFANetwork. Therefore it contains the state of each network's node (each of which is a BFA)
 * and the content of the network's links.
 *
 * @author Pietro Venturini
 */
public class BSState extends State {

    private Set<BFA> bfas;
    private Set<Link> links;

    public BSState(String name, Set<BFA> bfas, Set<Link> links) {
        super(name);
        this.bfas = bfas;
        this.links = links;
    }

    public BSState(String name, EnumSet<StateType> type, Set<BFA> bfas, Set<Link> links) {
        super(name, type);
        this.bfas = bfas;
        this.links = links;
    }

    public Set<BFA> getBfas() {
        return bfas;
    }

    public void setBfas(Set<BFA> bfas) {
        this.bfas = bfas;
    }

    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }
}
