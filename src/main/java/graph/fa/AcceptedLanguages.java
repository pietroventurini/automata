package graph.fa;

import com.google.common.collect.MoreCollectors;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableNetwork;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

/**
 * This class implements the functionality described by algorithm
 * EspressioniRegolari (page 17), allowing to retrieve the languages accepted by
 * a FA with multiple accepted states. FIXME: Rewrite and re-organize the code
 * in a better way !!!
 *
 * @author Giacomo Bontempi
 */
public final class AcceptedLanguages {
    private static final String EMPTY_STRING = "";
    private static MutableNetwork<State, Transition> network;
    private static List<State> acceptedStates;
    private static Map<Transition, State> markedTransitions;

    private AcceptedLanguages() {
    }

    /**
     * Reduce the FA to different regular expressions (one for each accepted state)
     * describing the languages accepted from the FA by applying the algorithm
     * RegularExpression(Nin) described at page 17 of the project description.
     * 
     * @param fa the finite automata
     * @return the accepted languages
     */
    public static final Set<String> reduceFAtoMultipleRegex(FA fa) {
        network = Graphs.copyOf(fa.getNetwork());

        // retrieve the accepted states in the original FA
        acceptedStates = network.nodes().stream().filter(State::isAcceptance).collect(Collectors.toList());

        // initialize the list of set of transitions marked
        markedTransitions = new HashMap<>();

        // retrieve the surrogate initial state and the surrogate acceptance state
        State n0 = createSurrogateInitialState();
        State nq = createSurrogateAcceptanceState();

        while (network.nodes().size() > 2 || areThereMultipleTransitionsWithSamePedix()) {
            if (thereIsASequenceOfTransitions()) {
                concatenateSequenceOfTransitions();
            } else if (thereAreParallelTransitions()) {
                reduceSetOfParallelTransitions();
            } else {
                reduceRemainingNodes();
            }
        }

        return network.edges().stream().map(t -> t.getSymbol()).collect(Collectors.toSet());
    }

    private static final boolean areThereMultipleTransitionsWithSamePedix() {
        for (State s : acceptedStates) {
            if (markedTransitions.keySet().stream().filter(t -> markedTransitions.get(t) == s)
                    .collect(Collectors.toSet()).size() > 1)
                return true;

        }
        return false;
    }

    /**
     * If there are ingoing graph.edges into the initial state beta0, create a
     * surrogate initial state n0 and an epsilon-transition from n0 to beta0.
     * 
     * @return the surrogate initial FA.State n0
     */
    private static final State createSurrogateInitialState() {
        // retrieve the initial state
        State n0 = network.nodes().stream().filter(State::isInitial).collect(MoreCollectors.onlyElement());

        if (network.inDegree(n0) > 0) {
            n0.isInitial(false);
            State beta0 = n0;
            n0 = new StateBuilder("n0").isInitial(true).build();
            network.addEdge(n0, beta0, new Transition(EMPTY_STRING));
        }
        return n0;
    }

    /**
     * If there are multiple acceptance states {beta_q} or if there are outgoing
     * graph.edges from the only acceptance state beta_q, create a surrogate
     * acceptance state nq and epsilon-transitions from each beta_q to nq.
     * 
     * @return the surrogate acceptance FA.State nq
     */
    private static final State createSurrogateAcceptanceState() {

        State nq = new StateBuilder("nq").isAcceptance(true).build();

        // if there are multiple acceptance states or a single one having outgoing
        // graph.edges, create surrogate acceptance state nq

        for (State beta_q : acceptedStates) {
            beta_q.isAcceptance(false);
            network.addEdge(beta_q, nq, new Transition(EMPTY_STRING)); // add eps-transition
        }

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
    private static final boolean thereIsASequenceOfTransitions() {
        return getSequenceOfTransitions().isEmpty() ? false : true;
    }

    private static final Set<State> getSequenceOfTransitions() {
        Set<State> sequences = network.nodes().stream()
                .filter(n -> network.inDegree(n) == 1 && network.outDegree(n) == 1
                        && !markedTransitions
                                .containsKey(network.inEdges(n).stream().collect(MoreCollectors.onlyElement())))
                .collect(Collectors.toSet());

        Set<State> nonMarkedSequences = sequences.stream().filter(
                n -> !markedTransitions.containsKey(network.outEdges(n).stream().collect(MoreCollectors.onlyElement())))
                .collect(Collectors.toSet());

        if (!nonMarkedSequences.isEmpty())
            return nonMarkedSequences;

        Set<State> markedSequences = sequences.stream().filter(
                n -> markedTransitions.containsKey(network.outEdges(n).stream().collect(MoreCollectors.onlyElement())))
                .collect(Collectors.toSet());

        if (!markedSequences.isEmpty())
            return markedSequences;

        return Collections.<State>emptySet();
    }

    /**
     * Check if there exist sets of parallel homogeneus transitions (homogeneus
     * means that all the transition are non marked or they all marked with the same
     * state). If so, return the first one found, otherwise return an empty set
     * 
     * @return a set of homogeneus parallel transitions
     */
    private static final Set<Transition> getParallelTransitions() {
        Set<Transition> transitions;
        for (State n1 : network.nodes()) {
            for (State n2 : network.nodes()) {
                transitions = Set.copyOf(network.edgesConnecting(n1, n2));
                transitions = filterHomogeneusTransitions(transitions);
                if (transitions.size() > 1)
                    return transitions;
            }
        }
        return Collections.<Transition>emptySet();
    }

    /**
     * Filter a set of parallel transition to make them homogeneous
     * 
     * @param transitions a set of parallel transitions
     * @return a set of homogeneus parallel transitions
     */
    public static final Set<Transition> filterHomogeneusTransitions(Set<Transition> transitions) {
        Set<Transition> nonMarked = transitions.stream().filter(t -> !markedTransitions.containsKey(t))
                .collect(Collectors.toSet());
        if (!nonMarked.isEmpty())
            return nonMarked;

        for (State s : acceptedStates) {
            Set<Transition> marked = transitions.stream()
                    .filter(t -> markedTransitions.containsKey(t) && markedTransitions.get(t) == s)
                    .collect(Collectors.toSet());
            if (!marked.isEmpty())
                return marked;
        }
        return Collections.<Transition>emptySet();
    }

    /**
     * (row 20 of page 18) Check if there is a pair of states with multiple parallel
     * transitions between them.
     * 
     * @return true such states exist, false otherwise
     */
    private static final boolean thereAreParallelTransitions() {
        return getParallelTransitions().isEmpty() ? false : true;
    }

    /**
     * (row 12 of page 18) Reduce a sequence of states having inDegree=outDegree=1
     * by merging them into a single transition from the first to the last state of
     * the original sequence, adding the subscripts to the transition when needed.
     * Note: differently from the algorithm from page 11, here we do not enclose the
     * new equivalent transition between brackets
     */
    private static final void concatenateSequenceOfTransitions() {

        State intermediate = getSequenceOfTransitions().iterator().next();

        // retrieve the predecessor node of the intermediate one
        State predecessor = network.predecessors(intermediate).stream().collect(MoreCollectors.onlyElement());
        // retrieve the successor node of the intermediate one
        State successor = network.successors(intermediate).stream().collect(MoreCollectors.onlyElement());

        Transition transitionFromPredecessorToIntermediate = network.edgeConnectingOrNull(predecessor, intermediate);

        Transition transitionFromIntemediateToSuccessor = network.edgeConnectingOrNull(intermediate, successor);

        // build the new transition by concatenating two adjacent transitions
        String newSymbol = new StringBuilder().append(transitionFromPredecessorToIntermediate.getSymbol())
                .append(transitionFromIntemediateToSuccessor.getSymbol()).toString();

        Transition newTransition = new Transition(newSymbol);

        if (!markedTransitions.containsKey(transitionFromIntemediateToSuccessor)) {
            if (successor.isAcceptance() || acceptedStates.contains(intermediate)) {
                markedTransitions.put(newTransition, intermediate);
            }
        } else {
            markedTransitions.put(newTransition, markedTransitions.get(transitionFromIntemediateToSuccessor));
            markedTransitions.remove(transitionFromIntemediateToSuccessor);
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
    private static final void reduceSetOfParallelTransitions() {

        // retrieve a set of parallel transitions
        Set<Transition> transitions = getParallelTransitions();

        boolean addSubscript = false;
        Transition anyTransition = transitions.iterator().next();
        State anyAcceptedState = null;

        if (markedTransitions.containsKey(anyTransition)) {
            addSubscript = true;
            anyAcceptedState = markedTransitions.get(anyTransition);
        }

        // build the new symbol which is the alternation between the symbols of each
        // transition
        String newSymbol = transitions.stream().map(Transition::getSymbol).collect(Collectors.joining("|", "(", ")"));

        // Retrieve the two endpoints (states) of the considered parallel transitions
        EndpointPair<State> stateEndpointPair = network.incidentNodes(transitions.stream().findAny().get());

        // remove old parallel transitions
        transitions.forEach(t -> network.removeEdge(t));

        // remove old parallel transitions from markedTransitions
        transitions.forEach(t -> markedTransitions.remove(t));

        // Create and add the new transition
        Transition newTransition = new Transition(newSymbol);
        if (addSubscript) {
            markedTransitions.put(newTransition, anyAcceptedState);
        }
        network.addEdge(stateEndpointPair, newTransition);
    }

    /**
     * this method is used in reduceRemainingNodes() to create the new transition
     */
    private static final void createNewTransition(Transition t1, Transition t2, State n1, State n2, State n,
            Optional<Transition> selfLoopTransition) {
        String newSymbol;
        if (selfLoopTransition.isPresent()) {
            newSymbol = new StringBuilder().append(t1.getSymbol())
                    .append("(" + selfLoopTransition.get().getSymbol() + ")*").append(t2.getSymbol()).toString();
            newSymbol = betweenBrackets(newSymbol);
        } else {
            newSymbol = t1.getSymbol().concat(t2.getSymbol());
        }
        Transition newTransition = new Transition(newSymbol);
        if (markedTransitions.containsKey(t2)) {
            markedTransitions.put(newTransition, markedTransitions.get(t2));
        } else if (n2.isAcceptance() && acceptedStates.contains(n)) {
            markedTransitions.put(newTransition, n);
        }
        network.addEdge(n1, n2, newTransition);

    }

    /**
     * (row 25-50 of page 19-20)
     */
    private static final void reduceRemainingNodes() {
        State n = network.nodes().stream()
                .filter(not((Predicate<State>) State::isInitial).and(not(State::isAcceptance))).findAny().get();

        // transitions of the form ( n'-> n )
        Set<Transition> ingoingTransitions = network.inEdges(n).stream()
                .filter(t -> !network.incidentNodes(t).nodeU().equals(n)).collect(Collectors.toSet());

        // transitions of the form ( n -> n'' ) with subscript
        Set<Transition> outTransitionsWithSubscript = network.outEdges(n).stream()
                .filter(t -> markedTransitions.containsKey(t) && !network.incidentNodes(t).nodeV().equals(n))
                .collect(Collectors.toSet());

        // transitions of the form ( n -> n'' ) without subscript
        Set<Transition> outTransitionsWithoutSubscript = network.outEdges(n).stream()
                .filter(t -> !markedTransitions.containsKey(t) && !network.incidentNodes(t).nodeV().equals(n))
                .collect(Collectors.toSet());

        Optional<Transition> selfLoopTransition = network.edgeConnecting(n, n);

        if (selfLoopTransition.isPresent()) {
            ingoingTransitions.remove(selfLoopTransition.get());
            outTransitionsWithSubscript.remove(selfLoopTransition.get());
            outTransitionsWithoutSubscript.remove(selfLoopTransition.get());

        }

        for (

        Transition t1 : ingoingTransitions) {
            for (Transition t2 : outTransitionsWithoutSubscript) {

                State n1 = network.incidentNodes(t1).nodeU();
                State n2 = network.incidentNodes(t2).nodeV();
                createNewTransition(t1, t2, n1, n2, n, selfLoopTransition);

            }
            for (Transition t2 : outTransitionsWithSubscript) {

                State n1 = network.incidentNodes(t1).nodeU();
                State n2 = network.incidentNodes(t2).nodeV();
                createNewTransition(t1, t2, n1, n2, n, selfLoopTransition);

            }
        }
        // remove node n and all its adjacent transitions
        network.removeNode(n);

        // remove old transitions from markedTransitions
        ingoingTransitions.forEach(t -> markedTransitions.remove(t));
        outTransitionsWithSubscript.forEach(t -> markedTransitions.remove(t));
        outTransitionsWithoutSubscript.forEach(t -> markedTransitions.remove(t));
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
