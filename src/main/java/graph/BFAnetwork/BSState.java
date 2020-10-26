package graph.BFAnetwork;

import graph.bfa.BFA;
import graph.fa.State;
import graph.fa.StateType;

import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * BSState is the State of a Behavioral Space, which has a more complex
 * structure than State of a FA. BSState represents a particular state
 * (snapshot) of a BFANetwork. Therefore it contains the state of each network's
 * node (each of which is a BFA) and the content of the network's links.
 *
 * @author Pietro Venturini
 */
public class BSState extends State {

    private Map<BFA, State> bfas;
    private Map<Link, String> links;

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
        if (this.links.values().stream().filter(l -> l != null).collect(Collectors.toSet()).isEmpty())
            this.isFinal(true);

    }

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
        return (this.bfas.equals(state.getBfas()) && this.links.equals(state.getLinks()));

    }

    public int hashCode() {
        return 1;
    }

    public String description() {
        String statesDescription = "";
        String linksDescription = "";
        for (State state : bfas.values()) {
            statesDescription = statesDescription + " " + state.getName();
        }
        for (String link : links.values()) {
            linksDescription = linksDescription + " " + link;
        }
        return statesDescription + " " + linksDescription;
    }
}
