import com.google.common.collect.MoreCollectors;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class implements the functionality described by algorithms EspressioneRegolare (page 9) and
 * EspressioniRegolari (page 17), allowing to retrieve the language accepted by a FA.
 */
public final class AcceptedLanguage {

    private AcceptedLanguage(){}

    /**
     * Reduce the FA to an equivalent regular expression describing the language accepted from the FA
     * by applying the algorithm RegularExpression(Nin) described at page 11 of the project description.
     * @param fa the finite automata
     * @return the accepted language
     */
    public static String reduceFAtoRegex(FA fa) {
        MutableNetwork<State, Transition> network = NetworkBuilder.from(fa.getNetwork()).build();

        // retrieve the surrogate initial state and the surrogate final state
        State n0 = createSurrogateInitialState(network);
        State nq = createSurrogateFinalState(network);

        while (network.edges().size() > 1) {
            //TODO: (16-17) if it exists a sequence of nodes with intermediate nodes having inDegree=outDegree=1 reduce it to a single transition
            //TODO: (18-19) else if it exists a set of parallel transitions, reduce it to a single transition
            //TODO: (20-32) else handle remaining nodes
        }

        return "";
    }

    /**
     * If there are ingoing edges into the initial state beta0, create a surrogate initial state n0
     * and an epsilon-transition from n0 to beta0.
     * @param network the underlying network of the current FA
     * @return the surrogate initial State n0
     */
    private static State createSurrogateInitialState(MutableNetwork<State, Transition> network) {
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
    private static State createSurrogateFinalState(MutableNetwork<State, Transition> network) {
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
}