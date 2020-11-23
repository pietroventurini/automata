package graph.BFAnetwork;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.graph.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

import graph.fa.FA;
import graph.fa.FABuilder;
import graph.bfa.BFA;
import graph.fa.State;
import graph.bfa.EventTransition;
import graph.fa.StateBuilder;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This class monitors and operates on the network of BFA; in particular, it is
 * able to retrieve all the current transitions enabled and execute a particular
 * transition enabled inside a BFA.
 *
 * @author Giacomo Bontempi
 * @author Pietro Venturini
 */

public final class BFANetworkSupervisor {

    private BFANetworkSupervisor() {
    }

    /**
     * Return all the links having the specified event inside their buffer.
     * 
     * @param links The set of links to be filtered
     * @param event the event to look for
     */
    private static final Set<Link> getLinksWithSpecifiedEvent(Set<Link> links, String event) {
        return links.stream().filter(l -> l.getEvent().get().equals(event)).collect(Collectors.toSet());
    }

    /**
     * Return all the links having an empty buffer.
     */
    private static final Set<Link> getEmptyLinks(Set<Link> links) {
        return links.stream().filter(l -> l.getEvent().isEmpty()).collect(Collectors.toSet());
    }

    /**
     * Return a set consisting of all the transition enabled inside the specified
     * BFA.
     */
    public static final Set<EventTransition> getTransitionsEnabledInBfa(BFANetwork bfaNetwork, BFA bfa) {
        Network<BFA, Link> network = Graphs.copyOf(bfaNetwork.getNetwork()); // FIXME: perché fare copia?
        Set<Link> inLinks = network.inEdges(bfa);
        // keep only non-empty inLinks
        inLinks = inLinks.stream().filter(l -> l.getEvent().isPresent()).collect(Collectors.toSet());
        Set<Link> outLinks = network.outEdges(bfa);

        Set<EventTransition> transitionsEnabled = new HashSet<>();
        for (EventTransition transition : bfa.getNetwork().outEdges(bfa.getCurrentState())) {

            boolean saveTransition = true;

            // check if the triggering event (if it exists) is missing on the incoming link
            if (transition.getInEvent().isPresent()
                    && getLinksWithSpecifiedEvent(inLinks, transition.getInEvent().get()).isEmpty())
                saveTransition = false;

            // check emptiness of every recipient link of transition
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
        Network<BFA, Link> network = Graphs.copyOf(bfaNetwork.getNetwork()); //FIXME: perché fare copia?
        Set<Link> inLinks = network.inEdges(bfa);
        inLinks = inLinks.stream().filter(l -> l.getEvent().isPresent()).collect(Collectors.toSet());
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

    /**
     * Retrieve the current state of a BFANetwork
     */
    public static final LOBSState getBFANetworkState(BFANetwork bfaNetwork) {
        Network<BFA, Link> network = bfaNetwork.getNetwork();
        Map<BFA, State> currentStates = new HashMap<>();
        Map<Link, String> links = new HashMap<>();

        String name = "";

        for (BFA bfa : network.nodes()) {
            currentStates.put(bfa, bfa.getCurrentState());
            name += " " + bfa.getCurrentState().getName();
        }

        for (Link link : network.edges()) {
            if (link.getEvent().isPresent()) {
                links.put(link, link.getEvent().get());
                name += " " + link.getEvent().get();
            } else {
                links.put(link, null);
                name += " eps";
            }
        }

        name = name.trim();

        return new LOBSState(name, currentStates, links);
    }

    /**
     * Rollback the BFANetwork to a certain state
     */
    public static final void rollbackBFANetwork(BSState state) {
        Map<BFA, State> newStates = state.getBfas();
        Map<Link, String> newLinks = state.getLinks();

        for (BFA bfa : newStates.keySet()) {
            bfa.setCurrentState(newStates.get(bfa));
        }

        for (Link link : newLinks.keySet()) {
            link.setEvent(newLinks.get(link));
        }

    }

    /**
     * Compute the behavioral space of the provided network of behavioral FAs.
     * @param bfaNetwork the network of behavioral FAs of which to compute the behavioral space
     * @return a FA representing the behavioral space that has been computed
     */
    public static final FA<BSState, BSTransition> getBehavioralSpace(BFANetwork bfaNetwork) {
        FABuilder<BSState, BSTransition> faBuilder = new FABuilder<>();

        // create the initial state
        BSState networkState = getBFANetworkState(bfaNetwork);
        networkState.isInitial(true);
        networkState.checkFinal();

        // pass the initial state to the FABuilder
        faBuilder.putState(networkState);

        // the set containing all the BSStates already explored
        Set<BSState> closed = new HashSet<>();

        // the set containing the BSStates to explore
        Set<BSState> toExplore = new HashSet<>();
        toExplore.add(networkState);

        while (!toExplore.isEmpty()) {
            BSState state = toExplore.iterator().next();
            rollbackBFANetwork(state);

            for (BFA bfa : bfaNetwork.getBFAs()) {
                for (EventTransition transition : getTransitionsEnabledInBfa(bfaNetwork, bfa)) {
                    executeTransition(bfaNetwork, bfa, transition);

                    BSState newState = getBFANetworkState(bfaNetwork);
                    newState.checkFinal();
                    if (!closed.contains(newState) && !toExplore.contains(newState)) {
                        toExplore.add(newState);
                        faBuilder.putState(newState);
                    }
                    faBuilder.putTransition(state, newState, new BSTransition(transition.getName(),
                            transition.getRelevanceLabel(), transition.getObservabilityLabel()));
                    rollbackBFANetwork(state);
                }
            }
            toExplore.remove(state);
            closed.add(state);
        }

        return faBuilder.build();

    }

    /**
     * Create the behavioral space related to a linear observation
     */
    public static final FA<LOBSState, BSTransition> getBehavioralSpaceForLinearObservation(BFANetwork bfaNetwork,
            ArrayList<String> linearObservation) {
        FABuilder<LOBSState, BSTransition> faBuilder = new FABuilder<>();

        // Construct the initial state of the behavioral space relative to linearObservation
        LOBSState networkState = getBFANetworkState(bfaNetwork);
        networkState.isInitial(true);
        networkState.setObservationIndex(0);
        faBuilder.putState(networkState);

        // the set containing the BSStates still to be explored
        Set<LOBSState> toExplore = new HashSet<>();
        toExplore.add(networkState);

        while (!toExplore.isEmpty()) {
            LOBSState state = toExplore.iterator().next();
            rollbackBFANetwork(state);
            for (BFA bfa : bfaNetwork.getBFAs()) {
                for (EventTransition transition : getTransitionsEnabledInBfa(bfaNetwork, bfa)) {
                    if (transition.getObservabilityLabel().equals("")
                            || (state.getObservationIndex() < linearObservation.size() && linearObservation
                                    .get(state.getObservationIndex()).equals(transition.getObservabilityLabel()))) {

                        executeTransition(bfaNetwork, bfa, transition);
                        LOBSState newState = getBFANetworkState(bfaNetwork);

                        if (!transition.getObservabilityLabel().equals(""))
                            newState.setObservationIndex(state.getObservationIndex() + 1);
                        else
                            newState.setObservationIndex(state.getObservationIndex());

                        if (newState.getObservationIndex() == linearObservation.size())
                            newState.checkFinal();

                        toExplore.add(newState);
                        faBuilder.putState(newState);

                        faBuilder.putTransition(state, newState, new BSTransition(transition.getName(),
                                transition.getRelevanceLabel(), transition.getObservabilityLabel()));
                        rollbackBFANetwork(state);
                    }

                }
            }
            toExplore.remove(state);

        }
        return faBuilder.build();
    }

    /**
     * Removes the states of the provided FA from which it can't be reached a final state.
     * @param fa the Finite automata to be pruned
     * @param <S> the type of states (nodes) of the FA
     * @return true if at least one state has been removed, false otherwise
     */
    public static <S extends State> boolean pruneFA(FA<S, ?> fa) {
        Set<S> toRemove = new HashSet<>();
        for (S s : fa.getStates()) {
            Set<S> reachableNodes = reachableNodes(fa.getNetwork(), s); // get the set of nodes reachable from s
            if (reachableNodes.stream().noneMatch(S::isFinal)) {
                toRemove.add(s);
            }
        }
        toRemove.forEach(s -> fa.getNetwork().removeNode(s));
        return toRemove.isEmpty() ? false : true;
    }

    /**
     * This method is inspired from Graphs.reachableNodes() of the guava graph library
     * Returns the set of nodes that are reachable from {@code node}. Node B is defined as reachable
     * from node A if there exists a path (a sequence of adjacent outgoing edges) starting at node A
     * and ending at node B. Note that a node is always reachable from itself via a zero-length path.
     *
     * <p>This is a "snapshot" based on the current topology of {@code network}, rather than a live view
     * of the set of nodes reachable from {@code node}. In other words, the returned {@link Set} will
     * not be updated after modifications to {@code network}.
     *
     * @throws IllegalArgumentException if {@code node} is not present in {@code network}
     *
     * FIXME: it looks like there's a bug (some of the states in the returned set are not the same of network
     */
    private static <N> Set<N> reachableNodes(Network<N,?> network, N node) {
        checkArgument(network.nodes().contains(node), "Node %s is not an element of this network.", node);
        return ImmutableSet.copyOf(Traverser.forGraph(network).breadthFirst(node));
    }


    /**
     * Compute the silent closure of {@code state} relative to {@code behavioralSpace}, by extracting the subspace
     * of the nodes that are reachable from {@code state} through non-observable transitions.
     * States of {@code behavioralSpace} that are final and exit states (those that has an observable outgoing
     * transition), are marked as acceptance states in the returned FA.
     *
     * @param behavioralSpace the behavioral space to which {@code state} must belong.
     * @param state the state of {@code behavioralSpace} whose silent closure we want to compute.
     * @return a FA corresponding to the silent closure of {@code state}
     */
    public static <S extends State> FA<S, BSTransition> silentClosure(FA<S, BSTransition> behavioralSpace, S state) {
        checkArgument(behavioralSpace.getStates().contains(state), "State %s does not belong to behavioral space %s", state, behavioralSpace);
        // check that state has at least one incoming observable transition
        checkArgument(behavioralSpace.getNetwork().inEdges(state).stream().anyMatch(BSTransition::hasObservabilityLabel), "The provided state has no incoming observable transition");

        MutableNetwork<S,BSTransition> network = Graphs.copyOf(behavioralSpace.getNetwork());

        // collect and remove observable transitions
        Set<BSTransition> observableTransitions = network.edges().stream().filter(BSTransition::hasObservabilityLabel).collect(Collectors.toSet());
        observableTransitions.forEach(t -> network.removeEdge(t));

        // find nodes reachable through non-observable transitions (since those transitions have just been removed)
        Set<S> nodes = reachableNodes(network, state);

        // keep only transitions between nodes both reachable from state
        MutableNetwork<S, BSTransition> inducedSubgraph = Graphs.inducedSubgraph(network, nodes);

        // collect all final and exit states into acceptance states
        Set<S> finalStates = inducedSubgraph.nodes().stream().filter(S::isFinal).collect(Collectors.toSet());
        Set<S> exitStates = inducedSubgraph.nodes().stream().filter(
                        s -> behavioralSpace.getNetwork()
                                .outEdges(s)
                                .stream()
                                .anyMatch(BSTransition::hasObservabilityLabel)
                ).collect(Collectors.toSet());

        Set<S> acceptanceStates = Sets.union(finalStates, exitStates);

        // mark acceptance states as such
        acceptanceStates.forEach(s -> s.isAcceptance(true));

        FA<S, BSTransition> silentClosure = new FA<>("", inducedSubgraph, state, acceptanceStates);
        return silentClosure;
    }
}