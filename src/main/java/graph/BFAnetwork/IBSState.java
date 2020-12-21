package graph.BFAnetwork;

import graph.bfa.BFA;
import graph.nodes.State;

import java.util.Map;

public interface IBSState extends State {

    Map<BFA, State> getBfas();

    void setBfas(Map<BFA, State> bfas);

    Map<Link, String> getLinks();

    void setLinks(Map<Link, String> links);
}
