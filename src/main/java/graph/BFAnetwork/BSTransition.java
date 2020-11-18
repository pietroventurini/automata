package graph.BFAnetwork;

import graph.edges.EdgeWithName;
import graph.fa.Transition;

public class BSTransition extends Transition implements EdgeWithName {

    private String observabilityLabel;
    private String name;

    public BSTransition(String name, String relevanceLabel, String observabilityLabel) {
        super(relevanceLabel);
        this.name = name;
        this.observabilityLabel = observabilityLabel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelevanceLabel() {
        return getSymbol();
    }

    public void setRelevanceLabel(String relevanceLabel) {
        setSymbol(relevanceLabel);
    }

    public String getObservabilityLabel() {
        return observabilityLabel;
    }

    public void setObservabilityLabel(String observabilityLabel) {
        this.observabilityLabel = observabilityLabel;
    }

}