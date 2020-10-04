import com.google.common.collect.MoreCollectors;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class implements the functionality described by algorithms EspressioneRegolare (page 9) and
 * EspressioniRegolari (page 17), allowing to retrieve the language accepted by a FA.
 * FIXME: Rewrite and restructure the code in a better way
 */
public final class AcceptedLanguage {

    private AcceptedLanguage(){}

    /**
     * Reduce the FA to an equivalent regular expression describing the language accepted from the FA
     * by applying the algorithm RegularExpression(Nin) described at page 11 of the project description.
     * @param fa the finite automata
     * @return the accepted language
     */
    public static final String reduceFAtoRegex(FA fa) {
        MutableNetwork<State, Transition> network = NetworkBuilder.from(fa.getNetwork()).build();

        // retrieve the surrogate initial state and the surrogate final state
        State n0 = createSurrogateInitialState(network);
        State nq = createSurrogateFinalState(network);

        boolean reduced;
        while (network.edges().size() > 1) {
            reduced = false;
            reduced = concatenateSequenceOfTransitions(network); // rows 16-17
            if (!reduced)
                reduced = reduceSetOfParallelTransitions(network); // rows 18-19
            if (!reduced)
                removeIntermediateNode(network); // rows 20-32
        }

        return network.edgeConnecting(n0, nq).get().getSymbol();
    }

    /**
     * If there are ingoing edges into the initial state beta0, create a surrogate initial state n0
     * and an epsilon-transition from n0 to beta0.
     * @param network the underlying network of the current FA
     * @return the surrogate initial State n0
     */
    private static final State createSurrogateInitialState(MutableNetwork<State, Transition> network) {
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
     * @param network the underlying network of the current FA
     * @return the surrogate final State nq
     */
    private static final State createSurrogateFinalState(MutableNetwork<State, Transition> network) {
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
     * Check if there is a sequence of nodes having inDegree=outDegree=1 and remove them by merging into
     * a single transition the entering transition and the outgoing one.
     * @return false if there isn't any node that can be removed, true if the network has been reduced by
     * concatenating a sequence of transitions according to the rule.
     */
    private static final boolean concatenateSequenceOfTransitions(MutableNetwork<State, Transition> network) {

        // retrieve, if it exists, a node with a single incident edge and a single outgoing edge
        State intermediate = network.nodes()
                                            .stream()
                                            .filter(n -> network.inDegree(n) == 1 && network.outDegree(n) == 1)
                                            .findAny()
                                            .get();
        if (intermediate == null)
            return false;

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
                return true;
            }
        }
    }

    private static final boolean reduceSetOfParallelTransitions(MutableNetwork<State, Transition> network) {
        //TODO: (18-19) else if it exists a set of parallel transitions, reduce it to a single transition
    }

    private static final boolean removeIntermediateNode(MutableNetwork<State, Transition> network) {
        //TODO: (20-32) else handle remaining nodes
    }
}