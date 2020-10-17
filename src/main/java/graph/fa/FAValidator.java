package graph.fa;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class provides some static methods that can be used to validate a FA.
 *
 * @author Pietro Venturini
 */
public class FAValidator {

    /**
     * Validate a FA entirely
     * @param fa the finite automata to validate
     * @return true if the fa is valid
     * @throws IllegalStateException if the fa is not valid (has isolated states / doesn't have any final state /
     *  has more than one initial state / doesn't have a final state)
     */
    public static boolean validate(FA fa) {
        if (hasOnlyOneInitialState(fa) && thereAreNotIsolatedStates(fa) && hasAtLeastOneFinalState(fa)) {
            return true;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Check whether a FA has exactly one initial state
     * @param fa the FA to check
     * @return true if fa has exactly one initial state, false otherwise
     */
    public static boolean hasOnlyOneInitialState(FA fa) {
        return fa.getNodes()
                .stream()
                .filter(State::isInitial)
                .count() == 1;
    }

    /**
     * Check that, unless the set of states contains only one state, then there are no isolated states
     */
    public static boolean thereAreNotIsolatedStates(FA fa) {
        if (fa.getStates().size() > 1)
            if (getIsolatedStates(fa).size() > 0)
                throw new IllegalStateException();
        return true;
    }

    public static boolean hasAtLeastOneFinalState(FA fa) {
        return fa.getFinalStates().size() > 0;
    }

    /**
     * @return the set of final states of the FA
     */
    private static Set<State> retrieveFinalStates(FA fa) {
        return fa.getNodes()
                .stream()
                .filter(State::isFinal)
                .collect(Collectors.toSet());
    }

    private static Set<State> getIsolatedStates(FA fa) {
        return fa.getNetwork().nodes()
                .stream()
                .filter(n -> fa.getNetwork().inDegree(n) == 0 && fa.getNetwork().outDegree(n) == 0)
                .collect(Collectors.toSet());
    }
}
