package graph.bfa;

import graph.edges.EdgeWithEvents;
import graph.edges.EdgeWithName;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * EventTransition represents a single transition of a behavioral FA
 * TODO: since transition's names must be unique, should we override equals?
 *
 * @author Pietro Venturini
 */
public class EventTransition implements EdgeWithName, EdgeWithEvents {

    private String name;
    private String inEvent;
    private Set<String> outEvents;

    /**
     * Builder used to construct instances of EventTransition
     */
    public static class Builder {
        // Required parameters
        private final String name;

        // Mandatory parameters initialized to default values
        private String inEvent = null;
        private Set<String> outEvents = new HashSet<>();

        /**
         * @param name the name of the transition
         */
        public Builder(String name) {
            this.name = name;
        }

        /**
         * Set the input event to the transition
         */
        public Builder inEvent(String inEvent) {
            this.inEvent = inEvent;
            return this;
        }

        /**
         * Add an output event to the transition
         * @param outEvent the output event to add
         */
        public Builder addOutEvent(String outEvent) {
            outEvents.add(outEvent);
            return this;
        }

        public EventTransition build() {
            return new EventTransition(this);
        }
    }

    private EventTransition(EventTransition.Builder builder) {
        this.name = builder.name;
        this.inEvent = builder.inEvent;
        this.outEvents = builder.outEvents;
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
    public Optional<String> getInEvent() {
        return Optional.<String>ofNullable(inEvent);
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
