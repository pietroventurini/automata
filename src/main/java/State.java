import java.util.EnumSet;

public class State implements Node {

    private String name; // maybe can change to a more complex type (e.g. see pag 35 of the project description)
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
     * @param type a set of StateType indicating the type of the state
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnumSet<StateType> getType() {
        return type;
    }

    public void setType(EnumSet<StateType> type) {
        this.type = type;
    }


}
