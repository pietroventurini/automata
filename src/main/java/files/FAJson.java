package files;

import com.google.common.graph.EndpointPair;
import graph.fa.FA;
import graph.fa.FABuilder;
import graph.fa.FAState;
import graph.fa.Transition;
import graph.nodes.State;

import java.util.*;

class FAJson {
    String name;
    String[] states;
    String initialState;
    String[] acceptanceStates;
    String[] finalStates;
    TransitionJson[] transitions;

    FAJson(String name, String[] states, String initialState, String[] acceptanceStates, String[] finalStates, TransitionJson[] transitions) {
        this.name = name;
        this.states = states;
        this.initialState = initialState;
        this.acceptanceStates = acceptanceStates;
        this.finalStates = finalStates;
        this.transitions = transitions;
    }

    /**
     * Alias of the other constructor but it does the unpacking
     */
    <S extends State, T extends Transition> FAJson(FA<S, T> fa) {
        this(
                fa.getName(),
                statesToArray(fa.getStates()),
                fa.getInitialState().getName(),
                statesToArray(fa.getAcceptanceStates()),
                statesToArray(fa.getFinalStates()),
                transitionsToArray(fa)
        );
    }

    /**
     * Given set of States, return an array containing their names
     *
     * e.g.
     * { S1:{name: "s1", ...}, S2:{name: "s2", ...}}
     * becomes
     * ["s1", "s2"]
     */
    static final <S extends State> String[] statesToArray(Set<S> states) {
        return states.stream().map(State::getName).toArray(String[]::new);
    }

    /**
     * Given fa, returns an array of JsonTransitions of the form:
     *  [
     *      {"source":"source.name", "target":"target.name", "symbol":"transition.symbol"},
     *      ...
     *      {"source":"source.name", "target":"target.name", "symbol":"transition.symbol"}
     *  ]
     */
    static final <S extends State, T extends Transition> TransitionJson[] transitionsToArray(FA<S,T> fa) {
        Set<T> transitions = fa.getTransitions();
        List<TransitionJson> list = new ArrayList<>();
        for (T t : transitions) {
            EndpointPair<S> incidentNodes = fa.getNetwork().incidentNodes(t);
            String source = incidentNodes.source().getName();
            String target = incidentNodes.target().getName();
            TransitionJson jsonTransition = new TransitionJson(source, target, t.getSymbol());
            list.add(jsonTransition);
        }
        return list.toArray(TransitionJson[]::new);
    }

    /**
     * Converts the JsonFA into a FA by invoking its builder.
     */
    FA<FAState, Transition> toFA() {
        FABuilder builder = new FABuilder<FAState, Transition>();

        // map stateName to FAState
        Map<String, FAState> namesToStates = new HashMap<>();
        for (String name : states) {
            namesToStates.putIfAbsent(name, new FAState(name));
        }

        builder.putStates(new HashSet<FAState>(namesToStates.values()));
        builder.putInitialState(namesToStates.get(initialState));

        for (String s : finalStates) {
            builder.putFinalState(namesToStates.get(s));
        }

        for (String s : acceptanceStates) {
            builder.putAcceptanceState(namesToStates.get(s));
        }

        for (TransitionJson t : transitions) {
            builder.putTransition(namesToStates.get(t.source), namesToStates.get(t.target), new Transition(t.symbol));
        }

        return builder.build();
    }
}
