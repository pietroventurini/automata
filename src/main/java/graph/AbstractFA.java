package graph;

import com.google.common.graph.MutableNetwork;
import graph.edges.Edge;
import graph.fa.State;

import java.util.Set;

/**
 * This class represents the common structure to all types of FAs. FAs, BFAs and Behavioral spaces (DFAs)
 * all have an initial state.
 * @param <S> The type of states (note: if we don't want to differentiate between FA's states and BFA's states
 *           then instead of having the type parameter E, we should fix the type argument State. i.e.
 *           declaring it as class AbstractFA<T extends Edge> extends Graph<State, T>
 * @param <T> The type of transitions
 */
public abstract class AbstractFA<S extends State, T extends Edge> extends Graph<S, T> {

    protected S initialState;

    public AbstractFA(MutableNetwork<S, T> network) {
        super(network);
    }

    public S getInitialState() {
        return initialState;
    }

    /**
     * This is an alias of Graph.getEdges()
     * @return the set of transitions in the underlying network
     */
    public Set<T> getTransitions() {
        return super.getEdges();
    }

    /**
     * This is an alias of Graph.getNodes()
     * @return the set of states in the underlying network
     */
    public Set<S> getStates() {
        return super.getNodes();
    }
}
