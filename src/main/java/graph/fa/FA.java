package graph.fa;

import com.google.common.graph.MutableNetwork;
import graph.AbstractFA;

import java.util.Set;

/**
 * This class represent a Finite Automata (or Finite State Machine), which
 * consists of a finite, non-empty set of states, where one is the initial
 * state, and a finite set of transitions. We distinguish acceptance states from
 * those which aren't.
 *
 * @author Pietro Venturini
 */
public class FA<S extends State, T extends Transition> extends AbstractFA<S, T> {

    private Set<S> finalStates;

    /**
     * Just for testing purposes, later on we will build it from a file containing
     * states and transitions
     * 
     * @param network      the underlying network of the FA
     * @param initialState the initial state of the FA
     * @param finalStates  the set (eventually empty) of final states
     */
    FA(MutableNetwork<S, T> network, S initialState, Set<S> finalStates) {
        super(network, initialState);
        this.finalStates = finalStates;
    }

    public Set<S> getFinalStates() {
        return finalStates;
    }

    /**
     * Reduce the FA to an equivalent regular expression describing the language
     * accepted from the FA by applying the algorithm RegularExpression(Nin)
     * described at page 11 of the project description.
     * 
     * @return the accepted language
     */
    public String regularExpression() {
        return AcceptedLanguage.reduceFAtoRegex(this);
    }

}
