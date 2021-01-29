package graph.bfa;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableNetwork;
import graph.AbstractFA;
import graph.nodes.State;

/**
 * This class represents a behavioral FA (BFA). It inherits all the methods of
 * its superclass AbstractFA. A BFA has a name and doesn't have final states.
 * The transitions have input and output events associated to them.
 *
 * @author Pietro Venturini
 */
public class BFA extends AbstractFA<State, EventTransition> {

    private State currentState;

    /**
     * Constructor of a Behavioral Finite Automata
     *
     * @param name         the name of the BFA
     * @param network      the underlying network of the BFA
     * @param initialState the initial state of the BFA
     * @param currentState the current state of the BFA
     */
    BFA(String name, MutableNetwork<State, EventTransition> network, State initialState, State currentState) {
        super(name, network, initialState);
        this.currentState = currentState;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    /**
     * Check if BFA is in its initial state
     * 
     * @return
     */
    public boolean isInitial() {
        return currentState == super.getInitialState();
    }

    public BFA copyOf(BFA bfa) {
        return new BFA(bfa.getName(), Graphs.copyOf(super.getNetwork()), super.getInitialState(), currentState);
    }

    public void printDescription() {
        MutableNetwork<State, EventTransition> network = this.getNetwork();
        System.out.println("\nBFA name: " + this.getName());
        System.out.println("\nList of states:");
        for (State s : this.getNodes()) {
            System.out.println("- " + s.getName());
        }
        System.out.println("\nList of transitions:");
        for (EventTransition e : this.getEdges()) {
            EndpointPair<State> pair = network.incidentNodes(e);
            System.out.println("- " + pair.nodeU().getName() + " -> " + e.getName() + " ->  " + pair.nodeV().getName());
        }
        System.out.println("\nInitial state: " + this.getInitialState().getName() + "\n\n");
    }

}
