package graph.bfa;

import graph.fa.FAState;
import graph.nodes.State;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class provides some static methods that can be used to validate a Behavioral FA (BFA).
 *
 * @author Pietro Venturini
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
        if (hasOnlyOneInitialState(bfa)
                && thereAreNotIsolatedStates(bfa)
                && doesNotHaveStatesWithDuplicateNames(bfa)
                && doesNotHaveTransitionsWithDuplicateNames(bfa)
            ) {
            return true;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Check that each state has a unique name.
     * @return false if there are different states with the same name, true if all the states have a unique name
     */
    private static boolean doesNotHaveStatesWithDuplicateNames(BFA bfa) {
        for (State s1 : bfa.getStates()) {
            for (State s2 : bfa.getStates()) {
                if (s1 != s2) {
                    if (s1.getName().equals(s2.getName())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Check that each transition has a unique name.
     * @return false if there are different transitions with the same name, true if all the transitions have a unique name
     */
    private static boolean doesNotHaveTransitionsWithDuplicateNames(BFA bfa) {
        for (EventTransition t1 : bfa.getTransitions()) {
            for (EventTransition t2 : bfa.getTransitions()) {
                if (t1 != t2) {
                    if (t1.getName().equals(t2.getName())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Check whether a BFA has exactly one initial state
     * @param bfa the FA to check
     * @return true if fa has exactly one initial state, false otherwise
     */
    public static boolean hasOnlyOneInitialState(BFA bfa) {
        return bfa.getInitialState() != null;
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
     * Get the isolated states of the BFA, i.e. the states with inDegree = outDegree = 0
     * @param bfa
     * @return a set containing all the isolated states
     */
    private static Set<State> getIsolatedStates(BFA bfa) {
        return bfa.getStates()
                .stream()
                .filter(n -> bfa.getNetwork().inDegree(n) == 0 && bfa.getNetwork().outDegree(n) == 0)
                .collect(Collectors.toSet());
    }
}
