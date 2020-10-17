package graph.bfa;

import graph.fa.FA;
import graph.fa.State;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class provides some static methods that can be used to validate a Behavioral FA (BFA).
 *
 */
public class BFAValidator {

    /**
     * Validate a BFA entirely
     * @param bfa the finite behavioral automata to validate
     * @return true if the fa is valid
     * @throws IllegalStateException if the bfa is not valid (has isolated states / has final states /
     *  has more than one initial state)
     */
    public static boolean validate(BFA bfa) {
        if (hasOnlyOneInitialState(bfa) && thereAreNotIsolatedStates(bfa) && doesNotHaveFinalStates(bfa)) {
            return true;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Check whether a BFA has exactly one initial state
     * @param bfa the FA to check
     * @return true if fa has exactly one initial state, false otherwise
     */
    public static boolean hasOnlyOneInitialState(BFA bfa) {
        return bfa.getNodes()
                .stream()
                .filter(State::isInitial)
                .count() == 1;
    }

    /**
     * Check that, unless the set of states contains only one state, then there are no isolated states
     */
    public static boolean thereAreNotIsolatedStates(BFA bfa) {
        if (bfa.getStates().size() > 1)
            if (getIsolatedStates(bfa).size() > 0)
                throw new IllegalStateException();
        return true;
    }

    /**
     * Check whether there aren't final states
     * @return true if no state is final, false otherwise
     */
    public static boolean doesNotHaveFinalStates(BFA bfa) {
        return bfa.getNodes()
                .stream()
                .noneMatch(State::isFinal);
    }

    /**
     * Get the isolated states of the BFA, i.e. the states with inDegree = outDegree = 0
     * @param bfa
     * @return a set containing all the isolated states
     */
    private static Set<State> getIsolatedStates(BFA bfa) {
        return bfa.getNodes()
                .stream()
                .filter(n -> bfa.getNetwork().inDegree(n) == 0 && bfa.getNetwork().outDegree(n) == 0)
                .collect(Collectors.toSet());
    }
}
