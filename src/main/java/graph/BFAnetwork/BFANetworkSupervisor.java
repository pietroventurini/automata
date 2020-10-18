package graph.BFAnetwork;

import com.google.common.graph.MutableNetwork;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import graph.bfa.BFA;
import graph.bfa.EventTransition;

/**
 * This class monitors and operates on the network of BFA; in particular, it is
 * able retrieves all the current transitions enabled and execute a particular
 * transition enabled inside a BFA.
 *
 */

public final class BFANetworkSupervisor {
    private static MutableNetwork<BFA, Link> network;
    private static Map<BFA, Set<EventTransition>> bfaAndTransitions; // this map represents the association between a
                                                                     // bfa and its transitions enbled

    private BFANetworkSupervisor() {
    }

    /**
     * This method returns all the links having a specified event inside their
     * buffer.
     *
     */
    private static final Set<Link> getLinksWithSpecifiedEvent(Set<Link> links, String event) {
        return links.stream().filter(l -> l.getEvent().equals(event)).collect(Collectors.toSet());
    }

    /**
     * This method returns all the links having an empty buffer.
     *
     */
    private static final Set<Link> getEmptyLinks(Set<Link> links) {
        return links.stream().filter(l -> l.getEvent().isPresent()).collect(Collectors.toSet());
    }

    /**
     * This method return a set consisting of all the transition enabled inside a
     * BFA.
     *
     */
    public static final Set<EventTransition> getTransitionsEnabledInBfa(BFA bfa) {
        Set<Link> inLinks = network.inEdges(bfa);
        Set<Link> outLinks = network.outEdges(bfa);

        Set<EventTransition> transitionsEnabled = Collections.emptySet();

        for (EventTransition transition : bfa.getEdges()) {
            boolean saveTransition = true;
            if (transition.getInEvent().isPresent()
                    && getLinksWithSpecifiedEvent(inLinks, transition.getInEvent().get()).isEmpty())
                saveTransition = false;

            if (getEmptyLinks(outLinks).size() < transition.getOutEvents().size())
                saveTransition = false;

            if (saveTransition)
                transitionsEnabled.add(transition);
        }

        return transitionsEnabled;
    }

    /**
     * This method adds to "bfaAndTransitions" every bfa with its transitions
     * enabled.
     * 
     *
     */
    public static final void getTransitionsEnabledInNetwork() {
        Set<BFA> bfas = network.nodes();

        bfaAndTransitions = new HashMap<>();

        for (BFA bfa : bfas) {
            bfaAndTransitions.put(bfa, getTransitionsEnabledInBfa(bfa));
        }
    }

    /**
     * This method executes a particular transition enabled inside a BFA
     * 
     *
     */
    public static final void executeTransition(BFA bfa, EventTransition transition) {
        Set<Link> inLinks = network.inEdges(bfa);
        Set<Link> outLinks = getEmptyLinks(network.outEdges(bfa));

        if (transition.getInEvent().isPresent())
            getLinksWithSpecifiedEvent(inLinks, transition.getInEvent().get()).iterator().next().removeEvent();

        for (String event : transition.getOutEvents()) {
            Link link = outLinks.iterator().next();
            link.setEvent(event);
            outLinks.remove(link);
        }
    }
}