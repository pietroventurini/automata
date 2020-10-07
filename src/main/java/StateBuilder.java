import java.util.EnumSet;

/**
 * Implementation of builder pattern for the State class
 */
public class StateBuilder {

    private String name;
    private EnumSet<StateType> type;

    /**
     * FIXME: is name mandatory? if not, we should replace this constructor, with one that does not require
     *  any parameter. If we do that, we must change also the build() method adding validation
     * @param name the name of the state
     */
    public StateBuilder (String name) {
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

    public State build() {
        // optional: add validatation
        return new State(name, type);
    }
}
