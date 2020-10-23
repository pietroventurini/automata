package graph.BFAnetwork;

import com.google.common.graph.Graphs;
import com.google.common.graph.MutableNetwork;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import graph.bfa.BFA;
import graph.bfa.EventTransition;

/**
 * This class monitors and operates on the network of BFA; in particular, it is
 * able retrieves all the current transitions enabled and execute a particular
 * transition enabled inside a BFA.
 *
 * @author Giacomo Bontempi
 */

public final class BFANetworkSupervisor {
    private static MutableNetwork<BFA, Link> network;

    private BFANetworkSupervisor() {
    }

    /**
     * Return all the links having the specified event inside their
     * buffer.
     * @param links The set of links to be filtered
     * @param event the event to look for
     */
    private static final Set<Link> getLinksWithSpecifiedEvent(Set<Link> links, String event) {
        return links.stream()
                .filter(l -> l.getEvent().get().equals(event))
                .collect(Collectors.toSet());
    }

    /**
     * Return all the links having an empty buffer.
     */
    private static final Set<Link> getEmptyLinks(Set<Link> links) {
        return links.stream()
                .filter(l -> l.getEvent().isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * Return a set consisting of all the transition enabled inside the specified
     * BFA.
     */
    public static final Set<EventTransition> getTransitionsEnabledInBfa(BFANetwork bfaNetwork, BFA bfa) {
        network = Graphs.copyOf(bfaNetwork.getNetwork());
        Set<Link> inLinks = network.inEdges(bfa);
        Set<Link> outLinks = network.outEdges(bfa);

        Set<EventTransition> transitionsEnabled = new HashSet<>();
        for (EventTransition transition : bfa.getNetwork().outEdges(bfa.getCurrentState())) {
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
     * Execute a particular transition enabled inside a BFA
     */
    public static final void executeTransition(BFANetwork bfaNetwork, BFA bfa, EventTransition transition) {
        network = Graphs.copyOf(bfaNetwork.getNetwork());
        Set<Link> inLinks = network.inEdges(bfa);
        Set<Link> outLinks = getEmptyLinks(network.outEdges(bfa));

        // if the transition has the inEvent, then I "pop" that event from the first
        // link containing such event in his buffer
        if (transition.getInEvent().isPresent())
            getLinksWithSpecifiedEvent(inLinks, transition.getInEvent().get()).iterator().next().removeEvent();

        // here I put all the outEvents of the transitions inside the empty links
        for (String event : transition.getOutEvents()) {
            Link link = outLinks.iterator().next();
            link.setEvent(event);
            outLinks.remove(link);
        }

        // here I update the current state
        bfa.setCurrentState(bfa.getNetwork().incidentNodes(transition).nodeV());

    }
}