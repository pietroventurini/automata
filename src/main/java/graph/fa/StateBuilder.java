package graph.fa;

import java.util.EnumSet;

/**
 * Implementation of builder pattern for the State class
 *
 * @author Pietro Venturini
 */
public class StateBuilder {

    private String name;
    private EnumSet<StateType> type;

    /**
     * FIXME: is name mandatory? if not, we should replace this constructor, with
     * one that does not require any parameter. If we do that, we must change also
     * the build() method adding validation
     * 
     * @param name the name of the state
     */
    public StateBuilder(String name) {
        this.name = name;
        this.type = EnumSet.noneOf(StateType.class);
    }

    public StateBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Specifies whether the state will be the initial state
     */
    public StateBuilder isInitial(boolean isInitial) {
        if (isInitial)
            type.add(StateType.INITIAL);
        else
            type.remove(StateType.INITIAL);
        return this;
    }

    /**
     * Specifies whether the state will be a final state
     */
    public StateBuilder isFinal(boolean isFinal) {
        if (isFinal)
            type.add(StateType.FINAL);
        else
            type.remove(StateType.FINAL);
        return this;
    }

    /**
     * Specifies whether the state will be an acceptance state
     */
    public StateBuilder isAcceptance(boolean isAcceptance) {
        if (isAcceptance)
            type.add(StateType.ACCEPTANCE);
        else
            type.remove(StateType.ACCEPTANCE);
        return this;
    }

    public State build() {
        return new State(name, type);
    }
}
