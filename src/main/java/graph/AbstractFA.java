package graph;

import com.google.common.graph.MutableNetwork;
import graph.edges.Edge;
import graph.nodes.State;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class represents the common structure to all types of FAs. FAs, BFAs and Behavioral spaces
 * all have an initial state.
 * @param <S> The type of states
 * @param <T> The type of transitions
 */
public abstract class AbstractFA<S extends State, T extends Edge> extends Graph<S, T> implements State {

    private String name;
    private S initialState;

    /**
     * Constructor of an Abstract finite automata. It has the parameters in common
     * to both FA and BFA.
     *
     * @param name         the name of the FA
     * @param network      the underlying network of the FA
     * @param initialState the initial state of the FA
     */
    public AbstractFA(String name, MutableNetwork<S, T> network, S initialState) {
        super(network);
        this.name = name;
        this.initialState = initialState;
    }

    @Override
    public String getName() { return name; }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public S getInitialState() {
        return initialState;
    }

    public void setInitialState(S initialState) {
        checkNotNull(initialState);
        this.initialState = initialState;
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

    /**
     * Check if {@code state} is an initial state
     */
    public boolean isInitial(S state) {
        return initialState == state;
    }


    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", initialState=" + initialState;
    }
}
