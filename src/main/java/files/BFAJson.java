package files;

import com.google.common.graph.EndpointPair;
import graph.bfa.BFA;
import graph.bfa.BFABuilder;
import graph.bfa.EventTransition;
import graph.fa.FAState;
import graph.nodes.State;

import java.util.*;

class BFAJson {
    String name;
    String[] states;
    String initialState;
    EventTransitionJson[] transitions;

    BFAJson(String name, String[] states, String initialState, EventTransitionJson[] transitions) {
        this.name = name;
        this.states = states;
        this.initialState = initialState;
        this.transitions = transitions;
    }

    /**
     * Alias of the other constructor but it does the unpacking
     */
    BFAJson(BFA bfa) {
        this(bfa.getName(), bfa.getStates().stream().map(State::getName).toArray(String[]::new),
                bfa.getInitialState().getName(), transitionsToArray(bfa));
    }

    /**
     * Given fa, returns an array of EventTransitionJson
     */
    static final EventTransitionJson[] transitionsToArray(BFA fa) {
        Set<EventTransition> transitions = fa.getTransitions();
        List<EventTransitionJson> list = new ArrayList<>();
        for (EventTransition t : transitions) {
            EndpointPair<State> incidentNodes = fa.getNetwork().incidentNodes(t);
            String source = incidentNodes.source().getName();
            String target = incidentNodes.target().getName();
            String[] outEvents = t.getOutEvents().toArray(String[]::new);
            EventTransitionJson jsonTransition = new EventTransitionJson(t.getName(), source, target,
                    t.getInEvent().orElse(null), outEvents, t.getObservabilityLabel(), t.getRelevanceLabel());
            list.add(jsonTransition);
        }
        return list.toArray(EventTransitionJson[]::new);
    }

    /**
     * Converts the JsonBFA into a BFA by invoking its builder.
     */
    BFA toBFA() {
        BFABuilder builder = new BFABuilder(name);

        // map stateName to FAState
        Map<String, State> namesToStates = new HashMap<>();
        for (String name : states) {
            namesToStates.putIfAbsent(name, new FAState(name));
        }

        builder.putStates(new HashSet<State>(namesToStates.values()));
        builder.putInitialState(namesToStates.get(initialState));

        for (EventTransitionJson t : transitions) {
            builder.putTransition(namesToStates.get(t.source), namesToStates.get(t.target), t.toEventTransition());
        }

        return builder.build();
    }
}
