package graph.bfa;

import graph.edges.EdgeWithEvents;
import graph.edges.EdgeWithName;

import java.util.Set;

/**
 * EventTransition represents a single transition of a behavioral FA
 */
public class EventTransition implements EdgeWithName, EdgeWithEvents {

    private String name;
    private String inEvent; //FIXME: 1) should it be an Optional<String>? 2) should we create a class Event?
    private Set<String> outEvents;

    public EventTransition(String name) {
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

    @Override
    public String getInEvent() {
        return inEvent;
    }

    @Override
    public void setInEvent(String inEvent) {
        this.inEvent = inEvent;
    }

    @Override
    public Set<String> getOutEvents() {
        return outEvents;
    }

    @Override
    public void setOutEvents(Set<String> outEvents) {
        this.outEvents = outEvents;
    }

    @Override
    public void addOutEvent(String outEvent) {
        outEvents.add(outEvent);
    }

    @Override
    public boolean removeOutEvent(String outEvent) {
        return outEvents.remove(outEvent);
    }
}
