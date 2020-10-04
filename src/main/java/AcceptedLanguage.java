import com.google.common.collect.MoreCollectors;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class implements the functionality described by algorithms EspressioneRegolare (page 9) and
 * EspressioniRegolari (page 17), allowing to retrieve the language accepted by a FA.
 * FIXME: Rewrite and restructure the code in a better way
 */
public final class AcceptedLanguage {

    private static MutableNetwork<State, Transition> network;

    private AcceptedLanguage(){}

    /**
     * Reduce the FA to an equivalent regular expression describing the language accepted from the FA
     * by applying the algorithm RegularExpression(Nin) described at page 11 of the project description.
     * @param fa the finite automata
     * @return the accepted language
     */
    public static final String reduceFAtoRegex(FA fa) {
        network = NetworkBuilder.from(fa.getNetwork()).build();

        // retrieve the surrogate initial state and the surrogate final state
        State n0 = createSurrogateInitialState();
        State nq = createSurrogateFinalState();

        while (network.edges().size() > 1) {
            if (thereIsASequenceOfTransitions()) {
                concatenateSequenceOfTransitions();
            } else if (thereAreParallelTransitions()) {
                reduceSetOfParallelTransitions(); // rows 18-19
            } else {
                reduceRemainingNodes(); // rows 20-32
            }
        }
        return network.edgeConnecting(n0, nq).get().getSymbol();
    }

    /**
     * If there are ingoing edges into the initial state beta0, create a surrogate initial state n0
     * and an epsilon-transition from n0 to beta0.
     * @return the surrogate initial State n0
     */
    private static final State createSurrogateInitialState() {
        // retrieve the initial state
        State n0 = network.nodes()
                .stream()
                .filter(State::isInitial)
                .collect(MoreCollectors.onlyElement());

        if (network.inDegree(n0) > 0) {
            n0.isInitial(false);
            State beta0 = n0;
            n0 = new StateBuilder("n0").isInitial(true).build();
            network.addEdge(n0, beta0, new Transition(""));
        }
        return n0;
    }

    /**
     * If there are multiple acceptance states {beta_q} or if there are outgoing edges from the only final state beta_q,
     * create a surrogate final state nq and epsilon-transitions from each beta_q to nq.
     * @return the surrogate final State nq
     */
    private static final State createSurrogateFinalState() {
        Set<State> finalStates = network.nodes()
                .stream()
                .filter(State::isFinal)
                .collect(Collectors.toSet());

        State nq = new StateBuilder("nq").isFinal(true).build();

        // if there is a single acceptance state, check if there are outgoing edges from it
        boolean outgoing = false;
        if (finalStates.size() == 1) {
            State onlyFinalState = finalStates.stream().collect(MoreCollectors.onlyElement());
            if (network.outDegree(onlyFinalState) > 0)
                outgoing = true;
            else
                nq = onlyFinalState;
        }

        // if there are multiple acceptance states or a single one having outgoing edges, create surrogate final state nq
        if (finalStates.size() > 1 || outgoing) {
            for (State beta_q : finalStates) {
                beta_q.isFinal(false);
                network.addEdge(beta_q, nq, new Transition("")); // add eps-transition
            }
        }

        return nq;
    }

    /**
     * (row 16 of page 11) Check if there is a sequence whose intermediate states have all only one entering transition
     * and only one outgoing one. We do that by just looking for a state with inDegree=outDegree=1.
     * @return true if such state exists, false otherwise
     */
    private static final boolean thereIsASequenceOfTransitions() {
        return network.nodes()
                    .stream()
                    .anyMatch(n -> network.inDegree(n) == 1 && network.outDegree(n) == 1);
    }

    /**
     * (row 17 of page 11) Reduce a sequence of states having inDegree=outDegree=1 by merging them into
     * a single transition from the first to the last state of the original sequence.
     */
    private static final void concatenateSequenceOfTransitions() {

        // retrieve, if it exists, a node with a single incident edge and a single outgoing edge
        State intermediate = network.nodes()
                                            .stream()
                                            .filter(n -> network.inDegree(n) == 1 && network.outDegree(n) == 1)
                                            .findAny()
                                            .get();
        if (intermediate == null)
            throw new NoSuchElementException();

        //retrieve the predecessor node of the intermediate one
        State predecessor = network.predecessors(intermediate)
                                    .stream()
                                    .collect(MoreCollectors.onlyElement());

        //retrieve the successor node of the intermediate one
        State successor = network.successors(intermediate)
                                .stream()
                                .collect(MoreCollectors.onlyElement());

        // while the sequence can still be reduced, merge pairs of transitions together
        while (true) {
            // build the new transition by concatenating two adjacent transitions
            String newSymbol = new StringBuilder().append(network.edgeConnectingOrNull(predecessor, intermediate).getSymbol())
                                                .append(network.edgeConnectingOrNull(intermediate, successor).getSymbol())
                                                .toString();
            Transition newTransition = new Transition(newSymbol);

            // remove the intermediate node and insert the new transition
            network.removeNode(intermediate);
            network.addEdge(predecessor, successor, newTransition);

            if (network.inDegree(predecessor) == 1) {
                intermediate = predecessor;
                predecessor = network.predecessors(intermediate).stream().collect(MoreCollectors.onlyElement());
            } else if (network.outDegree(successor) == 1) {
                intermediate = successor;
                successor = network.successors(intermediate).stream().collect(MoreCollectors.onlyElement());
            } else {
                return;
            }
        }
    }

    /**
     * (row 18 of page 11) Check if there is a pair of states with multiple parallel transitions between them.
     * @return true such states exist, false otherwise
     */
    private static final boolean thereAreParallelTransitions() {
        //TODO: implement method
        return false;
    }

    /**
     * (row 19 of page 11) Reduce a set of parallel transitions to a single transition which symbol is the
     * alternation between each transition's symbol.
     */
    private static final void reduceSetOfParallelTransitions() {
        //TODO: (18-19) else if it exists a set of parallel transitions, reduce it to a single transition
    }

    /**
     * (row 20-32 of page 11)
     */
    private static final void reduceRemainingNodes() {
        //TODO: (20-32) else handle remaining nodes
    }
}