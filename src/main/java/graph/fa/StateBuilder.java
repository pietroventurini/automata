package graph.fa;

/**
 * Implementation of builder pattern for the State class
 *
 * FIXME: Now that we have moved the state type into the FA, this builder offers no advantage
 *  to the constructor. We should remove it.
 *
 * @author Pietro Venturini
 */
public class StateBuilder {

    private String name;

    /**
     * @param name the name of the state
     */
    public StateBuilder(String name) {
        this.name = name;
    }

    public StateBuilder name(String name) {
        this.name = name;
        return this;
    }


    public FAState build() {
        return new FAState(name);
    }
}
