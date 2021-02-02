package graph.fa;

/**
 * Implementation of builder pattern for the State class
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
