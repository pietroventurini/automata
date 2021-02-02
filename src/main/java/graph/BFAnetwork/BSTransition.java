package graph.BFAnetwork;

import graph.bfa.BFA;
import graph.edges.EdgeWithName;
import graph.fa.Transition;

public class BSTransition extends Transition implements EdgeWithName {

    private String observabilityLabel;
    private String relevanceLabel;
    private String name;

    public BSTransition(String name, String relevanceLabel, String observabilityLabel) {
        this.name = name;
        this.observabilityLabel = observabilityLabel;
        this.relevanceLabel = relevanceLabel;
    }

    @Override
    public String getSymbol() {
        return relevanceLabel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    /**
     * check that the transition has an observability label
     */
    public boolean hasObservabilityLabel() {
        return observabilityLabel != null && !observabilityLabel.isEmpty();
    }

    public void setObservabilityLabel(String observabilityLabel) {
        this.observabilityLabel = observabilityLabel;
    }

    /**
     * check that the transition has a relevance label
     */
    public boolean hasRelevanceLabel() {
        return relevanceLabel != null && !relevanceLabel.isEmpty();
    }


    @Override
    public String toString() {
        return "name: " + name + '\n' +
        "observability label: " + observabilityLabel + '\n' +
        "relevance label: " + relevanceLabel;
    }
}