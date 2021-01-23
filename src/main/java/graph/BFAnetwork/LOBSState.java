package graph.BFAnetwork;

import graph.bfa.BFA;
import graph.nodes.State;

import java.util.Map;
import java.util.Objects;

/**
 * LOBSState is the State of a Behavioral Space related to a linear observation.
 * This class just extends BSState by adding an observation index related to the
 * label of the next expected observable event in the linear observation.
 */
public class LOBSState extends BSState {

    private int observationIndex;

    public LOBSState(String name, Map<BFA, State> bfas, Map<Link, String> links) {
        super(name, bfas, links);
    }

    public void setObservationIndex(int observationIndex) {
        this.observationIndex = observationIndex;
    }

    public int getObservationIndex() {
        return observationIndex;
    }

    /**
     * Override the equals method: two LOBSStates are equals if and only if the
     * current state is the same for each BFA, each link has the same content and
     * they have the same observation index.
     */
    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        // Check if 'o' is an instance of BSState
        if (!(o instanceof LOBSState)) {
            return false;
        }

        LOBSState state = (LOBSState) o;
        return (this.getBfas().equals(state.getBfas()) && this.getLinks().equals(state.getLinks())
                && this.observationIndex == state.getObservationIndex() && this.getName().equals(state.getName()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getBfas(), this.getLinks(), observationIndex);
    }
}
