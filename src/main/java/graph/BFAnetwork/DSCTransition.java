package graph.BFAnetwork;

import graph.fa.Transition;

/**
 * It represents a transition of a Decorated Space of Closures (DSC), like at page 69 of the project description.
 * Such a transition inherits the name and the observability label from the behavioral space from which that space
 * has been computed.
 * Furthermore, it has a symbol, which is the decoration of the exit-state in its source silent closure
 */
public class DSCTransition extends Transition {

    private String name;
    private String observabilityLabel;

    public DSCTransition(String name, String symbol, String observabilityLabel) {
        super(symbol);
        this.name = name;
        this.observabilityLabel = observabilityLabel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObservabilityLabel() {
        return observabilityLabel;
    }

    public void setObservabilityLabel(String observabilityLabel) {
        this.observabilityLabel = observabilityLabel;
    }

    public boolean hasObservabilityLabel() {
        return observabilityLabel != null && !observabilityLabel.isEmpty();
    }
}