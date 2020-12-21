package graph.fa;

import graph.nodes.State;

import java.util.Set;
import java.util.function.Predicate;
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
    public static <S extends State, T extends Transition> boolean validate(FA<S,T> fa) {
        if (hasOnlyOneInitialState(fa) && thereAreNotIsolatedStates(fa) && hasAtLeastOneAcceptanceState(fa)) {
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
    public static <S extends State, T extends Transition> boolean hasOnlyOneInitialState(FA<S,T> fa) {
        return fa.getInitialState() != null;
    }

    /**
     * Check that, unless the set of states contains only one state, then there are no isolated states
     */
    public static <S extends State, T extends Transition> boolean thereAreNotIsolatedStates(FA<S,T> fa) {
        Set<S> isolatedStates = getIsolatedStates(fa);
        if (isolatedStates.isEmpty() || fa.getStates().size() == 1) {
            return true;
        }
        throw new IllegalStateException();
    }

    public static <S extends State, T extends Transition> boolean hasAtLeastOneAcceptanceState(FA<S,T> fa) {
        return fa.getAcceptanceStates().size() > 0;
    }


    private static <S extends State, T extends Transition> Set<S> getIsolatedStates(FA<S,T> fa) {
        return fa.getNodes()
                .stream()
                .filter(n -> fa.getNetwork().inDegree(n) == 0 && fa.getNetwork().outDegree(n) == 0)
                .collect(Collectors.toSet());
    }
}
