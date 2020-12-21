package graph.BFAnetwork;

import graph.bfa.BFA;
import graph.nodes.State;

import java.util.Map;

/**
 * Use decorator pattern
 */
public class DecoratedBSState implements IBSState {
    private BSState state;
    private String decoration;

    public DecoratedBSState(BSState state, String decoration) {
        this.state = state;
        this.decoration = decoration;
    }

    @Override
    public void setName(String name) {
        state.setName(name);
    }

    @Override
    public String getName() {
        return state.getName();
    }

    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }

    @Override
    public Map<BFA, State> getBfas() {
        return state.getBfas();
    }

    @Override
    public void setBfas(Map<BFA, State> bfas) {
        state.setBfas(bfas);
    }

    @Override
    public Map<Link, String> getLinks() {
        return state.getLinks();
    }

    @Override
    public void setLinks(Map<Link, String> links) {
        state.setLinks(links);
    }

    public BSState getBSState() {
        return state;
    }
}
