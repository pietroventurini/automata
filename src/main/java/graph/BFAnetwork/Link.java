package graph.BFAnetwork;

import java.util.Optional;
import graph.edges.EdgeWithName;

/**
 * This class represents a single link connecting two BFAs inside a BFANetwork.
 *
 * @author Giacomo Bontempi
 */
public class Link implements EdgeWithName {
    private String name;
    private String event; // the only event that can be contained in a Link

    public Link(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Optional<String> getEvent() {
        return Optional.<String>ofNullable(event);
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void removeEvent() {
        this.event = null;
    }
}
