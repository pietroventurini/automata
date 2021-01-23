package graph.bfa;

import graph.edges.EdgeWithEvents;
import graph.edges.EdgeWithName;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * EventTransition represents a single transition of a behavioral FA
 */
public class EventTransition implements EdgeWithName, EdgeWithEvents {

    private String name;
    private String inEvent;
    private Set<String> outEvents;
    private String observabilityLabel;
    private String relevanceLabel;

    /**
     * Builder used to construct instances of EventTransition
     */
    public static class Builder {
        // Required parameters
        private final String name;

        // Mandatory parameters initialized to default values
        private String inEvent = null;
        private Set<String> outEvents = new HashSet<>();
        private String observabilityLabel = "";
        private String relevanceLabel = "";

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
         * Add a set of output events to the transition
         * @param outEvents the set of output events to add
         */
        public Builder outEvents(Set<String> outEvents) {
            this.outEvents.addAll(outEvents);
            return this;
        }

        /**
         * Add an output event to the transition
         * 
         * @param outEvent the output event to add
         */
        public Builder addOutEvent(String outEvent) {
            this.outEvents.add(outEvent);
            return this;
        }

        /**
         * Set the observability label
         * 
         * @param label the observability label
         */
        public Builder observabilityLabel(String label) {
            this.observabilityLabel = label;
            return this;
        }

        /**
         * Set the relevance label
         * 
         * @param label the relevance label
         */
        public Builder relevanceLabel(String label) {
            this.relevanceLabel = label;
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
        this.observabilityLabel = builder.observabilityLabel;
        this.relevanceLabel = builder.relevanceLabel;
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
    public void removeInEvent() {
        this.inEvent = null;
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

    public String getObservabilityLabel() {
        return observabilityLabel;
    }

    public void setObservabilityLabel(String label) {
        this.observabilityLabel = label;
    }

    public String getRelevanceLabel() {
        return relevanceLabel;
    }

    public void setRelevanceLabel(String label) {
        this.relevanceLabel = label;
    }
}
