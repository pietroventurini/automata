package graph.fa;

import com.google.common.collect.MoreCollectors;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableNetwork;
import graph.nodes.State;

import java.util.*;
import java.util.stream.Collectors;

import static graph.fa.Constants.EPS;

/**
 * This class implements the functionality described by algorithm
 * EspressioniRegolari (page 17), allowing to retrieve the languages accepted by
 * a FA with multiple accepted states.
 *
 * @author Giacomo Bontempi
 */
public final class AcceptedLanguages {

    private AcceptedLanguages() {
    }

    /**
     * Returns the set of accepted languages. This method exists for backward
     * compatibility reasons. You should prefer the method reduceFAtoMapOfRegex in
     * order to preserve the information about what language is accepted by each
     * acceptance state.
     * 
     * @param finiteAutomata the FA of which to compute the set of accepted
     *                       languages
     */
    public static final <S extends State, T extends Transition> Set<String> reduceFAtoMultipleRegex(
            FA<S, T> finiteAutomata) {
        return new HashSet<String>(reduceFAtoMapOfRegex(finiteAutomata).values());
    }

    /**
     * Compute the language accepted by each acceptance state of the provided FA by
     * applying the algorithm RegularExpressions(Nin) described at page 17 of the
     * project description.
     *
     * @param finiteAutomata the finite automata of which to compute the accepted
     *                       languages
     * @return a Map associating to each acceptance state the corresponding accepted
     *         language
     *
     *         FIXME: Se markedTransitions fosse una mappa <Stato, Transizione>
     *         invece che <Transizione, Stato>? In quel modo potremmo direttamente
     *         restituire la mappa <stato --> markedTransition.getSymbol()>
     */
    public static final <S extends State, T extends Transition> Map<S, String> reduceFAtoMapOfRegex(
            FA<S, T> finiteAutomata) {
        // create a copy of the provided finite automata, on which we will work
        FA<S, T> fa = FA.copyOf(finiteAutomata);
        MutableNetwork<S, T> network = fa.getNetwork();

        // retrieve the acceptance states in the original FA
        List<S> acceptanceStates = new ArrayList<>(fa.getAcceptanceStates());

        // initialize the map of transitions marked
        Map<T, S> markedTransitions = new HashMap<>();

        // retrieve the surrogate initial state and the surrogate acceptance state
        S n0 = createSurrogateInitialState(fa);
        S nq = createSurrogateAcceptanceState(fa);

        while (network.nodes().size() > 2
                || areThereMultipleTransitionsWithSamePedix(acceptanceStates, markedTransitions)) {
            if (thereIsASequenceOfTransitions(fa, markedTransitions)) {
                concatenateSequenceOfTransitions(fa, markedTransitions, acceptanceStates);
            } else if (thereAreParallelTransitions(fa, markedTransitions, acceptanceStates)) {
                reduceSetOfParallelTransitions(fa, markedTransitions, acceptanceStates);
            } else {
                reduceRemainingNodes(fa, markedTransitions, acceptanceStates);
            }
        }

        // construct and return a map <state --> languageAcceptedByState>
        Map<S, String> acceptedLang = new HashMap<>();

        // if the network is trivial, then return eps
        if (acceptanceStates.size() == 1
                && finiteAutomata.getNetwork().outDegree(finiteAutomata.getInitialState()) == 0) {
            acceptedLang.put(finiteAutomata.getInitialState(), EPS);
            return acceptedLang;
        }

        for (T t : network.outEdges(n0)) {
            acceptedLang.put(markedTransitions.get(t), t.getSymbol());
        }
        return acceptedLang;
    }

    private static final <S extends State, T extends Transition> boolean areThereMultipleTransitionsWithSamePedix(
            List<S> acceptedStates, Map<T, S> markedTransitions) {
        for (S s : acceptedStates) {
            if (markedTransitions.keySet().stream().filter(t -> markedTransitions.get(t) == s).count() > 1) { // FIXME:
                                                                                                              // '==' o
                                                                                                              // 'equals'?
                return true;
            }
        }
        return false;
    }

    /**
     * If there are ingoing edges into the initial state, create a surrogate initial
     * state n0 and an epsilon-transition from n0 to initialState.
     * 
     * @return the surrogate initial State n0
     */
    private static final <S extends State, T extends Transition> S createSurrogateInitialState(FA<S, T> fa) {
        S initialState = fa.getInitialState();
        if (fa.getNetwork().inDegree(initialState) > 0) {
            // initialState.isInitial(false);
            S beta0 = initialState;
            initialState = (S) new StateBuilder("n0").build();
            fa.getNetwork().addEdge(initialState, beta0, (T) new Transition(EPS));
            fa.setInitialState(initialState);
        }
        return initialState;
    }

    /**
     * If there are multiple acceptance states {beta_q} or if there are outgoing
     * graph.edges from the only acceptance state beta_q, create a surrogate
     * acceptance state nq and epsilon-transitions from each beta_q to nq.
     * 
     * @return the surrogate acceptance State nq
     */
    private static final <S extends State, T extends Transition> S createSurrogateAcceptanceState(FA<S, T> fa) {

        Set<S> acceptanceStates = fa.getAcceptanceStates();
        S nq = (S) new StateBuilder("nq").build();

        // if there are multiple acceptance states or a single one having outgoing
        // edges, create surrogate acceptance state nq
        for (S beta_q : fa.getAcceptanceStates()) {
            // beta_q.isAcceptance(false);
            fa.getNetwork().addEdge(beta_q, nq, (T) new Transition(EPS)); // add eps-transition
        }

        fa.setAcceptanceStates(new HashSet<S>(Arrays.asList(nq)));
        return nq;
    }

    /**
     * (row 12 and 18 of page 11) Check if there is a sequence whose intermediate
     * states have all only one entering transition and only one outgoing one and
     * the outgoing one is marked. We do that by just looking for a state with
     * inDegree=outDegree=1.
     * 
     * @return true if such state exists, false otherwise
     */
    private static final <S extends State, T extends Transition> boolean thereIsASequenceOfTransitions(FA<S, T> fa,
            Map<T, S> markedTransitions) {
        return getSequenceOfTransitions(fa, markedTransitions).isEmpty() ? false : true;
    }

    private static final <S extends State, T extends Transition> Set<S> getSequenceOfTransitions(FA<S, T> fa,
            Map<T, S> markedTransitions) {
        MutableNetwork<S, T> network = fa.getNetwork();
        Set<S> sequences = network.nodes().stream().filter(n -> network.inDegree(n) == 1 && network.outDegree(n) == 1
                && !markedTransitions.containsKey(network.inEdges(n).stream().collect(MoreCollectors.onlyElement())))
                .collect(Collectors.toSet());

        Set<S> nonMarkedSequences = sequences.stream().filter(
                n -> !markedTransitions.containsKey(network.outEdges(n).stream().collect(MoreCollectors.onlyElement())))
                .collect(Collectors.toSet());

        if (!nonMarkedSequences.isEmpty())
            return nonMarkedSequences;

        Set<S> markedSequences = sequences.stream().filter(
                n -> markedTransitions.containsKey(network.outEdges(n).stream().collect(MoreCollectors.onlyElement())))
                .collect(Collectors.toSet());

        // FIXME: non potremmo sostituire tutto quello che segue con "return
        // markedSequences;" ??
        if (!markedSequences.isEmpty())
            return markedSequences;

        return Collections.<S>emptySet();
    }

    /**
     * Check if there exist sets of parallel homogeneus transitions (homogeneus
     * means that all the transition are non marked or they all marked with the same
     * state). If so, return the first one found, otherwise return an empty set
     * 
     * @return a set of homogeneous parallel transitions
     */
    private static final <S extends State, T extends Transition> Set<T> getParallelTransitions(FA<S, T> fa,
            Map<T, S> markedTransitions, List<S> acceptedStates) {
        MutableNetwork<S, T> network = fa.getNetwork();
        Set<T> transitions;
        for (S n1 : network.nodes()) {
            for (S n2 : network.nodes()) {
                transitions = Set.copyOf(network.edgesConnecting(n1, n2));
                transitions = filterHomogeneusTransitions(transitions, markedTransitions, acceptedStates);
                if (transitions.size() > 1)
                    return transitions;
            }
        }
        return Collections.<T>emptySet();
    }

    /**
     * Filter a set of parallel transition to make them homogeneous
     * 
     * @param transitions a set of parallel transitions
     * @return a set of homogeneus parallel transitions
     */
    public static final <S extends State, T extends Transition> Set<T> filterHomogeneusTransitions(Set<T> transitions,
            Map<T, S> markedTransitions, List<S> acceptedStates) {
        Set<T> nonMarked = transitions.stream().filter(t -> !markedTransitions.containsKey(t))
                .collect(Collectors.toSet());
        if (!nonMarked.isEmpty())
            return nonMarked;

        for (S s : acceptedStates) {
            Set<T> marked = transitions.stream()
                    .filter(t -> markedTransitions.containsKey(t) && markedTransitions.get(t) == s)
                    .collect(Collectors.toSet());
            if (!marked.isEmpty())
                return marked;
        }
        return Collections.<T>emptySet();
    }

    /**
     * (row 20 of page 18) Check if there is a pair of states with multiple parallel
     * transitions between them.
     * 
     * @return true such states exist, false otherwise
     */
    private static final <S extends State, T extends Transition> boolean thereAreParallelTransitions(FA<S, T> fa,
            Map<T, S> markedTransitions, List<S> acceptedStates) {
        return getParallelTransitions(fa, markedTransitions, acceptedStates).isEmpty() ? false : true;
    }

    /**
     * (row 12 of page 18) Reduce a sequence of states having inDegree=outDegree=1
     * by merging them into a single transition from the first to the last state of
     * the original sequence, adding the subscripts to the transition when needed.
     * Note: differently from the algorithm from page 11, here we do not enclose the
     * new equivalent transition between brackets
     */
    private static final <S extends State, T extends Transition> void concatenateSequenceOfTransitions(FA<S, T> fa,
            Map<T, S> markedTransitions, List<S> acceptedStates) {
        MutableNetwork<S, T> network = fa.getNetwork();
        S intermediate = getSequenceOfTransitions(fa, markedTransitions).iterator().next();

        // retrieve the predecessor node of the intermediate one
        S predecessor = network.predecessors(intermediate).stream().collect(MoreCollectors.onlyElement());
        // retrieve the successor node of the intermediate one
        S successor = network.successors(intermediate).stream().collect(MoreCollectors.onlyElement());

        T transitionFromPredecessorToIntermediate = network.edgeConnectingOrNull(predecessor, intermediate);

        T transitionFromIntermediateToSuccessor = network.edgeConnectingOrNull(intermediate, successor);

        // build the new transition by concatenating two adjacent transitions
        String newSymbol = new StringBuilder().append(transitionFromPredecessorToIntermediate.getSymbol())
                .append(transitionFromIntermediateToSuccessor.getSymbol()).toString();

        T newTransition = (T) new Transition(newSymbol);

        if (!markedTransitions.containsKey(transitionFromIntermediateToSuccessor)) {
            if (fa.isAcceptance(successor) || acceptedStates.contains(intermediate)) {
                newSymbol = new StringBuilder().append(transitionFromPredecessorToIntermediate.getSymbol()).toString();
                newTransition = (T) new Transition(newSymbol);
                markedTransitions.put(newTransition, intermediate);
            }
        } else {
            markedTransitions.put(newTransition, markedTransitions.get(transitionFromIntermediateToSuccessor));
            markedTransitions.remove(transitionFromIntermediateToSuccessor);
        }
        // remove the intermediate node and insert the new transition
        network.removeNode(intermediate);

        network.addEdge(predecessor, successor, newTransition);

    }

    /**
     * (row 20 of page 18) Reduce a set of parallel transitions to a single
     * transition which symbol is the alternation between each transition's symbol,
     * adding the subscripts to the new transition when needed.
     */
    private static final <S extends State, T extends Transition> void reduceSetOfParallelTransitions(FA<S, T> fa,
            Map<T, S> markedTransitions, List<S> acceptedStates) {

        MutableNetwork<S, T> network = fa.getNetwork();

        // retrieve a set of parallel transitions
        Set<T> transitions = getParallelTransitions(fa, markedTransitions, acceptedStates);

        boolean addSubscript = false;
        T anyTransition = transitions.iterator().next();
        S anyAcceptedState = null;

        if (markedTransitions.containsKey(anyTransition)) {
            addSubscript = true;
            anyAcceptedState = markedTransitions.get(anyTransition);
        }

        // build the new symbol which is the alternation between the symbols of each
        // transition
        String newSymbol = transitions.stream().map(T::getSymbol).collect(Collectors.joining("|", "(", ")"));

        // Retrieve the two endpoints (states) of the considered parallel transitions
        EndpointPair<S> stateEndpointPair = network.incidentNodes(transitions.stream().findAny().get());

        // remove old parallel transitions
        transitions.forEach(t -> network.removeEdge(t));

        // remove old parallel transitions from markedTransitions
        transitions.forEach(t -> markedTransitions.remove(t));

        // Create and add the new transition
        T newTransition = (T) new Transition(newSymbol);
        if (addSubscript) {
            markedTransitions.put(newTransition, anyAcceptedState);
        }
        network.addEdge(stateEndpointPair, newTransition);
    }

    /**
     * this method is used in reduceRemainingNodes() to create the new transition
     */
    private static final <S extends State, T extends Transition> void createNewTransition(FA<S, T> fa,
            Map<T, S> markedTransitions, List<S> acceptedStates, T t1, T t2, S n1, S n2, S n,
            Optional<T> selfLoopTransition) {
        MutableNetwork<S, T> network = fa.getNetwork();
        String newSymbol;
        if (selfLoopTransition.isPresent()) {
            newSymbol = new StringBuilder().append(t1.getSymbol())
                    .append("(" + selfLoopTransition.get().getSymbol() + ")*").append(t2.getSymbol()).toString();
            newSymbol = betweenBrackets(newSymbol);
        } else {
            newSymbol = t1.getSymbol().concat(t2.getSymbol());
        }
        T newTransition = (T) new Transition(newSymbol);

        if (markedTransitions.containsKey(t2)) {
            markedTransitions.put(newTransition, markedTransitions.get(t2));
        } else if (fa.isAcceptance(n2) && acceptedStates.contains(n)) {
            if (selfLoopTransition.isPresent()) {
                newSymbol = new StringBuilder().append(t1.getSymbol())
                        .append("(" + selfLoopTransition.get().getSymbol() + ")*").toString();
                newSymbol = betweenBrackets(newSymbol);
            } else {
                newSymbol = t1.getSymbol();
            }
            newTransition = (T) new Transition(newSymbol);
            markedTransitions.put(newTransition, n);
        }
        network.addEdge(n1, n2, newTransition);

    }

    /**
     * (row 25-50 of page 19-20)
     */
    private static final <S extends State, T extends Transition> void reduceRemainingNodes(FA<S, T> fa,
            Map<T, S> markedTransitions, List<S> acceptedStates) {
        MutableNetwork<S, T> network = fa.getNetwork();
        // S n = network.nodes().stream().filter(not((Predicate<S>)
        // S::isInitial).and(not(S::isAcceptance))).findAny().get();
        S n = network.nodes().stream()
                .filter(s -> !s.equals(fa.getInitialState()) && !fa.getAcceptanceStates().contains(s)).findAny().get();

        // transitions of the form ( n'-> n )
        Set<T> ingoingTransitions = network.inEdges(n).stream()
                .filter(t -> !network.incidentNodes(t).nodeU().equals(n) && !markedTransitions.containsKey(t))
                .collect(Collectors.toSet());

        // transitions of the form ( n -> n'' ) with subscript
        Set<T> outTransitionsWithSubscript = network.outEdges(n).stream()
                .filter(t -> markedTransitions.containsKey(t) && !network.incidentNodes(t).nodeV().equals(n))
                .collect(Collectors.toSet());

        // transitions of the form ( n -> n'' ) without subscript
        Set<T> outTransitionsWithoutSubscript = network.outEdges(n).stream()
                .filter(t -> !markedTransitions.containsKey(t) && !network.incidentNodes(t).nodeV().equals(n))
                .collect(Collectors.toSet());

        Optional<T> selfLoopTransition = network.edgeConnecting(n, n);

        if (selfLoopTransition.isPresent()) {
            ingoingTransitions.remove(selfLoopTransition.get());
            outTransitionsWithSubscript.remove(selfLoopTransition.get());
            outTransitionsWithoutSubscript.remove(selfLoopTransition.get());
        }

        for (

        T t1 : ingoingTransitions) {
            for (T t2 : outTransitionsWithoutSubscript) {

                S n1 = network.incidentNodes(t1).nodeU();
                S n2 = network.incidentNodes(t2).nodeV();
                createNewTransition(fa, markedTransitions, acceptedStates, t1, t2, n1, n2, n, selfLoopTransition);

            }
            for (T t2 : outTransitionsWithSubscript) {

                S n1 = network.incidentNodes(t1).nodeU();
                S n2 = network.incidentNodes(t2).nodeV();
                createNewTransition(fa, markedTransitions, acceptedStates, t1, t2, n1, n2, n, selfLoopTransition);

            }
        }
        // remove old transitions from markedTransitions
        ingoingTransitions.forEach(t -> markedTransitions.remove(t));
        outTransitionsWithSubscript.forEach(t -> markedTransitions.remove(t));
        outTransitionsWithoutSubscript.forEach(t -> markedTransitions.remove(t));

        // remove node n and all its adjacent transitions
        network.removeNode(n);

    }

    /**
     * Enclose a string within brackets, for example "abc" becomes "(abc)"
     * 
     * @param expression the string to enclose
     * @return the string enclosed within brackets FIXME: move method to a utility
     *         class
     */
    private static String betweenBrackets(String expression) {
        return "(" + expression + ")";
    }

}
