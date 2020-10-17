package graph.edges;

import java.util.Optional;
import java.util.Set;

/**
 * This interface represents an edge with events associated to it. Examples of
 * such edges are Behavioral FAs' Transitions, Network of BFAs' Links, Behavioral Spaces' transitions.
 * FIXME: should this be an abstract class?
 *
 * @author Pietro Venturini
 */
public interface EdgeWithEvents extends Edge {

    /**
     * @return the input event of the edge
     */
    Optional<String> getInEvent();

    /**
     * Set the input event of the edge
     */
    void setInEvent(String name);

    /**
     * @return the set of output events
     */
    Set<String> getOutEvents();

    /**
     * Set the specified set ot output events
     */
    void setOutEvents(Set<String> outEvents);

    /**
     * Add the specified output event to the set of output events
     * @param outEvent the output event to add
     */
    void addOutEvent(String outEvent);

    /**
     * Remove the specified output event
     * @param outEvent the output event to remove
     * @return true if the output event was successfully removed
     */
    boolean removeOutEvent(String outEvent);
}
