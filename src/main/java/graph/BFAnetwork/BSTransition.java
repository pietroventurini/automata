package graph.BFAnetwork;

import graph.fa.Transition;

public class BSTransition extends Transition {

    private String observabilityLabel;
    private String relevanceLabel;

    public BSTransition(String name, String relevanceLabel, String observabilityLabel) {
        super(name);
        this.observabilityLabel = observabilityLabel;
        this.relevanceLabel = relevanceLabel;
    }

    public String getRelevanceLabel() {
        return relevanceLabel;
    }

    public void setRelevanceLabel(String relevanceLabel) {
        this.relevanceLabel = relevanceLabel;
    }

    public String getObservabilityLabel() {
        return observabilityLabel;
    }

    public void setObservabilityLabel(String observabilityLabel) {
        this.observabilityLabel = observabilityLabel;
    }
}