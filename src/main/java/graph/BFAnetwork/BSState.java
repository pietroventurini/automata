package graph.BFAnetwork;

import graph.bfa.BFA;
import graph.nodes.State;

import java.util.Map;
import java.util.Objects;

/**
 * BSState is the State of a Behavioral Space, which has a more complex
 * structure than State of a FA. BSState represents a particular state
 * (snapshot) of a BFANetwork. Therefore it contains the state of each network's
 * node (each of which is a BFA) and the content of the network's links.
 *
 * @author Pietro Venturini
 * @author Giacomo Bontempi
 */
public class BSState implements IBSState {

    private String name;
    private Map<BFA, State> bfas; // this map stores for each BFA (in a BFANetwork) a copy of its current state
    private Map<Link, String> links; // this map stores for each Link (in a BFANetwork) a copy of the event inside
                                     // its buffer

    public BSState(String name, Map<BFA, State> bfas, Map<Link, String> links) {
        this.name = name;
        this.bfas = bfas;
        this.links = links;
    }

    @Override
    public Map<BFA, State> getBfas() {
        return bfas;
    }

    @Override
    public void setBfas(Map<BFA, State> bfas) {
        this.bfas = bfas;
    }

    @Override
    public Map<Link, String> getLinks() {
        return links;
    }

    @Override
    public void setLinks(Map<Link, String> links) {
        this.links = links;
    }

    /**
     * Check if the BSState is final (i.e. if it represents a configuration of the
     * BFANetwork in which all the Links are empty)
     */
    public boolean isFinal() {
        return this.links.values().stream().allMatch(Objects::isNull);
    }

    /**
     * Here we rewrite the equals method: two BSStates are equals if and only if the
     * current state is the same for each BFA and each link has the same content
     */
    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        // Check if 'o' is an instance of BSState
        if (!(o instanceof BSState)) {
            return false;
        }

        BSState state = (BSState) o;
        return (bfas.equals(state.getBfas()) && links.equals(state.getLinks())
                && this.getName().equals(state.getName()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(bfas, links);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: " + name + '\n' + "STATES" + '\n');
        for (BFA bfa : bfas.keySet()) {
            sb.append("\tBFA: " + bfa.getName() + ", state: " + bfas.get(bfa).getName() + '\n');
        }
        sb.append("LINKS" + '\n');
        for (Link l : links.keySet()) {
            // l.getEvent().orElse("ε")
            if (links.get(l) == null)
                sb.append("\tLink: " + l.getName() + ", event: " + "ε" + '\n');
            else
                sb.append("\tLink: " + l.getName() + ", event: " + links.get(l) + '\n');
        }
        return sb.toString();
    }
}
