package graph.fa;

import com.google.common.graph.MutableNetwork;
import graph.AbstractFA;

import java.util.Objects;
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

    private Set<S> acceptanceStates;

    /**
     * Constructor of a Finite Automata.
     *
     * @param name         the name of the FA
     * @param network      the underlying network of the FA
     * @param initialState the initial state of the FA
     * @param acceptanceStates  the set (eventually empty) of acceptance states
     */
    public FA(String name, MutableNetwork<S, T> network, S initialState, Set<S> acceptanceStates) {
        super(name, network, initialState);
        this.acceptanceStates = acceptanceStates;
    }

    public Set<S> getAcceptanceStates() {
        return acceptanceStates;
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
