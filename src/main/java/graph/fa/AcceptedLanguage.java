package graph.fa;

import com.google.common.collect.MoreCollectors;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableNetwork;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

/**
 * This class implements the functionality described by algorithms
 * EspressioneRegolare (page 9) and EspressioniRegolari (page 17), allowing to
 * retrieve the language accepted by a FA.
 * FIXME: Rewrite and re-organize the code in a better way !!!
 * FIXME: use generics and not raw types.
 *
 * @author Pietro Venturini
 */
public final class AcceptedLanguage {
    private static final String EMPTY_STRING = "";
    private static MutableNetwork<State, Transition> network;

    private AcceptedLanguage() {
    }

    /**
     * Reduce the FA to an equivalent regular expression describing the language
     * accepted from the FA by applying the algorithm RegularExpression(Nin)
     * described at page 11 of the project description.
     * 
     * @param fa the finite automata
     * @return the accepted language
     */
    public static final String reduceFAtoRegex(FA fa) {

        network = Graphs.copyOf(fa.getNetwork());

        // retrieve the surrogate initial state and the surrogate acceptance state
        State n0 = createSurrogateInitialState();
        State nq = createSurrogateAcceptanceState();
        while (network.edges().size() > 1) {
            if (thereIsASequenceOfTransitions()) {
                concatenateSequenceOfTransitions();
            } else if (thereAreParallelTransitions()) {
                reduceSetOfParallelTransitions();
            } else {
                reduceRemainingNodes();
            }
        }
        Optional<Transition> onlyTransition = network.edgeConnecting(n0, nq);
        if (onlyTransition.isEmpty())
            return EMPTY_STRING;
        return onlyTransition.get().getSymbol();
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
        Set<State> acceptanceStates = network.nodes().stream().filter(State::isAcceptance).collect(Collectors.toSet());

        State nq = new StateBuilder("nq").isAcceptance(true).build();

        // if there is a single acceptance state, check if there are outgoing
        // edges from it
        boolean outgoing = false;
        if (acceptanceStates.size() == 1) {
            State onlyAcceptanceState = acceptanceStates.stream().collect(MoreCollectors.onlyElement());
            if (network.outDegree(onlyAcceptanceState) > 0)
                outgoing = true;
            else
                nq = onlyAcceptanceState;
        }

        // if there are multiple acceptance states or a single one having outgoing
        // edges, create surrogate acceptance state nq
        if (acceptanceStates.size() > 1 || outgoing) {
            for (State beta_q : acceptanceStates) {
                beta_q.isAcceptance(false);
                network.addEdge(beta_q, nq, new Transition(EMPTY_STRING)); // add eps-transition
            }
        }

        return nq;
    }

    /**
     * (row 16 of page 11) Check if there is a sequence whose intermediate states
     * have all only one entering transition and only one outgoing one. We do that
     * by just looking for a state with inDegree=outDegree=1.
     * 
     * @return true if such state exists, false otherwise
     */
    private static final boolean thereIsASequenceOfTransitions() {
        return network.nodes().stream().anyMatch(n -> network.inDegree(n) == 1 && network.outDegree(n) == 1);
    }

    /**
     * (row 17 of page 11) Reduce a sequence of states having inDegree=outDegree=1
     * by merging them into a single transition from the first to the last state of
     * the original sequence. Note: differently from the algorithm from page 11,
     * here we do not enclose the new equivalent transition between brackets
     */
    private static final void concatenateSequenceOfTransitions() {

        // retrieve, if it exists, a node with a single incident edge and a single
        // outgoing edge
        State intermediate = network.nodes().stream().filter(n -> network.inDegree(n) == 1 && network.outDegree(n) == 1)
                .findAny().get();
        if (intermediate == null)
            throw new NoSuchElementException();

        // retrieve the predecessor node of the intermediate one
        State predecessor = network.predecessors(intermediate).stream().collect(MoreCollectors.onlyElement());

        // retrieve the successor node of the intermediate one
        State successor = network.successors(intermediate).stream().collect(MoreCollectors.onlyElement());

        // build the new transition by concatenating two adjacent transitions
        String newSymbol = new StringBuilder()
                .append(network.edgeConnectingOrNull(predecessor, intermediate).getSymbol())
                .append(network.edgeConnectingOrNull(intermediate, successor).getSymbol()).toString();
        Transition newTransition = new Transition(newSymbol);

        // remove the intermediate node and insert the new transition
        network.removeNode(intermediate);
        network.addEdge(predecessor, successor, newTransition);

    }

    /**
     * Check if there exist sets of parallel transitions, if so, return the first
     * one found, otherwise return an empty set
     * 
     * @return
     */
    private static final Set<Transition> getParallelTransitions() {
        Set<Transition> transitions;
        for (State n1 : network.nodes()) {
            for (State n2 : network.nodes()) {
                transitions = Set.copyOf(network.edgesConnecting(n1, n2));
                if (transitions.size() > 1)
                    return transitions;
            }
        }
        return Collections.<Transition>emptySet();
    }

    /**
     * (row 18 of page 11) Check if there is a pair of states with multiple parallel
     * transitions between them.
     * 
     * @return true such states exist, false otherwise
     */
    private static final boolean thereAreParallelTransitions() {
        return getParallelTransitions().isEmpty() ? false : true;
    }

    /**
     * (row 19 of page 11) Reduce a set of parallel transitions to a single
     * transition which symbol is the alternation between each transition's symbol.
     */
    private static final void reduceSetOfParallelTransitions() {

        // retrieve a set of parallel transitions
        Set<Transition> transitions = getParallelTransitions();

        // build the new symbol which is the alternation between the symbols of each
        // transition
        String newSymbol = transitions.stream().map(Transition::getSymbol).collect(Collectors.joining("|", "(", ")"));

        // Retrieve the two endpoints (states) of the considered parallel transitions
        EndpointPair<State> stateEndpointPair = network.incidentNodes(transitions.stream().findAny().get());

        // remove old parallel transitions
        transitions.forEach(t -> network.removeEdge(t));

        // Create and add the new transition
        network.addEdge(stateEndpointPair, new Transition(newSymbol));
    }

    /**
     * (row 20-32 of page 11)
     */
    private static final void reduceRemainingNodes() {
        State n = network.nodes().stream()
                .filter(not((Predicate<State>) State::isInitial).and(not(State::isAcceptance))).findAny().get();

        Set<Transition> ingoingTransitions = network.inEdges(n); // transitions of the form ( n' -> n )
        Set<Transition> outgoingTransitions = network.outEdges(n); // transitions of the form ( n -> n" )
        Optional<Transition> selfLoopTransition = network.edgeConnecting(n, n);

        for (Transition t1 : ingoingTransitions) {
            for (Transition t2 : outgoingTransitions) {
                // if none between t1 and t2 is the self-loop transition
                if (!t1.equals(selfLoopTransition.get()) && !t2.equals(selfLoopTransition.get())) {
                    State n1 = network.incidentNodes(t1).nodeU();
                    State n2 = network.incidentNodes(t2).nodeV();
                    if (selfLoopTransition.isPresent()) {
                        String newSymbol = new StringBuilder().append(t1.getSymbol())
                                .append("(" + selfLoopTransition.get().getSymbol() + ")*").append(t2.getSymbol())
                                .toString();
                        network.addEdge(n1, n2, new Transition(betweenBrackets(newSymbol)));
                    } else {
                        network.addEdge(n1, n2, new Transition(t1.getSymbol().concat(t2.getSymbol())));
                    }
                }
            }
        }
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