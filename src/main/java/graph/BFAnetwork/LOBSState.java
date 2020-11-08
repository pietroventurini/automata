package graph.BFAnetwork;

import java.util.Map;

import graph.bfa.BFA;
import graph.fa.State;

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
                && this.observationIndex == state.getObservationIndex());

    }

    public int hashCode() {
        return 1;
    }
}
