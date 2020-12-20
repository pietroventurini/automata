package graph.fa;

import com.google.common.graph.Graphs;
import com.google.common.graph.MutableNetwork;
import graph.AbstractFA;
import graph.nodes.Node;

import java.util.Set;

/**
 * This class represent a Finite Automata (or Finite State Machine), which
 * consists of a finite, non-empty set of states, where one is the initial
 * state, and a finite set of transitions. We distinguish acceptance states from
 * those which aren't.
 *
 * TODO: since we added setter methods, we lost guarantee of the fa's validity. We should check validation after
 *  invoking any mutator method.
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

    public void setAcceptanceStates(Set<S> acceptanceStates) {
        this.acceptanceStates = acceptanceStates;
    }

    /**
     * Returns a copy of {@code fa}, having same name, initial state, acceptance states and
     * underlying network.
     */
    public static <S extends State, T extends Transition> FA<S,T> copyOf(FA<S,T> fa) {
        return new FA(fa.getName(), Graphs.copyOf(fa.getNetwork()),
                fa.getInitialState(), fa.getAcceptanceStates());
    }

}
