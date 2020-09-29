import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

public class FABuilder extends GraphBuilder<State, Transition> {

    /**
     * Invokes the constructor of the superclass, which instantiates the underlying network
     */
    public FABuilder() {
        super();
        /* here we can set additional properties of network (which is inherited from GraphBuilder)
         * that are specific for a FA. Examples of properties are: allowSelfLoops,
         * immutable, edgeOrder, nodeOrder... */
    }

    public FA build() {
        // TODO: validate the FA before creating it: check that exists one and only one initial state
        // validate()
        return new FA(this.network);
    }
}
