package graph.fa;

import graph.nodes.Node;

import java.util.EnumSet;

/**
 * This class represents a state of a FA. A state is a node of the underlying graph, with a type associated (described
 * by the StateType enum) and a unique name.
 *
 * @author Pietro Venturini
 */
public class State implements Node {

    private String name;
    private EnumSet<StateType> type;

    /**
     * Initialize a state that isn't initial neither final
     */
    public State(String name) {
        this.name = name;
        this.type = EnumSet.noneOf(StateType.class);
    }

    /**
     * Initialize a state given its name and type
     * @param type a set of FA.StateType indicating the type of the state
     */
    public State(String name, EnumSet<StateType> type) {
        this.name = name;
        this.type = type;
    }

    public boolean isInitial() {
        return this.type.contains(StateType.INITIAL);
    }

    public boolean isFinal() {
        return this.type.contains(StateType.FINAL);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public EnumSet<StateType> getType() {
        return type;
    }

    public void setType(EnumSet<StateType> type) {
        this.type = type;
    }

    public void isInitial(boolean isInitial) {
        if (isInitial) {
            type.add(StateType.INITIAL);
        } else {
            type.remove(StateType.INITIAL);
        }
    }

    public void isFinal(boolean isFinal) {
        if (isFinal) {
            type.add(StateType.FINAL);
        } else {
            type.remove(StateType.FINAL);
        }
    }


}
