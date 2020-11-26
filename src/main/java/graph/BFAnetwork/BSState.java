package graph.BFAnetwork;

import graph.bfa.BFA;
import graph.fa.State;
import graph.fa.StateType;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * BSState is the State of a Behavioral Space, which has a more complex
 * structure than State of a FA. BSState represents a particular state
 * (snapshot) of a BFANetwork. Therefore it contains the state of each network's
 * node (each of which is a BFA) and the content of the network's links.
 *
 * @author Pietro Venturini
 * @author Giacomo Bontempi
 */
public class BSState extends State {

    private Map<BFA, State> bfas; // this map stores for each BFA (in a BFANetwork) a copy of its current state
    private Map<Link, String> links; // this map stores for each Link (in a BFANetwork) a copy of the event inside
                                     // its buffer

    public BSState(String name, Map<BFA, State> bfas, Map<Link, String> links) {
        super(name);
        this.bfas = bfas;
        this.links = links;
    }

    public BSState(String name, EnumSet<StateType> type, Map<BFA, State> bfas, Map<Link, String> links) {
        super(name, type);
        this.bfas = bfas;
        this.links = links;
    }

    public Map<BFA, State> getBfas() {
        return bfas;
    }

    public void setBfas(Map<BFA, State> bfas) {
        this.bfas = bfas;
    }

    public Map<Link, String> getLinks() {
        return links;
    }

    public void setLinks(Map<Link, String> links) {
        this.links = links;
    }

    /**
     * Check if the BSState is final (i.e. if it represents a configuration of the
     * BFANetwork in which all the Links are empty)
     */
    public void checkFinal() {
        if (this.links.values().stream().allMatch(Objects::isNull))
            this.isFinal(true);
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
        return (bfas.equals(state.getBfas()) && links.equals(state.getLinks()));

    }

    @Override
    public int hashCode() {
        return Objects.hash(bfas, links);
    }
}
