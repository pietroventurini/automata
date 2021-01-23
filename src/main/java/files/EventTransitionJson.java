package files;

import graph.bfa.EventTransition;

import java.util.Set;

class EventTransitionJson {
    String name;
    String source;
    String target;
    String inEvent;
    String[] outEvents;
    String observabilityLabel;
    String relevanceLabel;

    EventTransitionJson(String name, String source, String target, String inEvent, String[] outEvents, String observabilityLabel, String relevanceLabel) {
        this.name = name;
        this.source = source;
        this.target = target;
        this.inEvent = inEvent;
        this.outEvents = outEvents;
        this.observabilityLabel = observabilityLabel;
        this.relevanceLabel = relevanceLabel;
    }

    /**
     * Converts the EventTransitionJson into an EventTransition by invoking its builder.
     */
    EventTransition toEventTransition() {
        return new EventTransition.Builder(name)
                .inEvent(inEvent)
                .outEvents(Set.of(outEvents))
                .observabilityLabel(observabilityLabel)
                .relevanceLabel(relevanceLabel)
                .build();
    }
}
