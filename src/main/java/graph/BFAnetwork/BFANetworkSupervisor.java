package graph.BFAnetwork;

import com.google.common.collect.MoreCollectors;
import com.google.common.collect.Sets;
import com.google.common.graph.*;

import java.security.InvalidAlgorithmParameterException;
import java.util.*;
import java.util.stream.Collectors;

import graph.fa.*;
import graph.bfa.BFA;
import graph.bfa.EventTransition;
import graph.nodes.State;

import static com.google.common.base.Preconditions.checkArgument;
import static graph.fa.Constants.EPS;

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
        Network<BFA, Link> network = bfaNetwork.getNetwork();
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
        Network<BFA, Link> network = bfaNetwork.getNetwork();
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
    public static final BSState getBFANetworkState(BFANetwork bfaNetwork) {
        Network<BFA, Link> network = bfaNetwork.getNetwork();
        Map<BFA, State> currentStates = new HashMap<>();
        Map<Link, String> links = new HashMap<>();

        String name = "";

        List<BFA> orderedBfas = network.nodes().stream().sorted(Comparator.comparing(BFA::getName))
                .collect(Collectors.toList());
        List<Link> orderedLinks = network.edges().stream().sorted(Comparator.comparing(Link::getName))
                .collect(Collectors.toList());

        for (BFA bfa : orderedBfas) {
            currentStates.put(bfa, bfa.getCurrentState());
            name += " " + bfa.getCurrentState().getName();
        }

        for (Link link : orderedLinks) {
            if (link.getEvent().isPresent()) {
                links.put(link, link.getEvent().get());
                name += " " + link.getEvent().get();
            } else {
                links.put(link, null);
                name += " eps";
            }
        }

        name = name.trim();

        return new BSState(name, currentStates, links);
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
     *
     * @param bfaNetwork the network of behavioral FAs of which to compute the
     *                   behavioral space
     * @return a FA representing the behavioral space that has been computed
     */
    public static final FA<BSState, BSTransition> getBehavioralSpace(BFANetwork bfaNetwork) {
        FABuilder<BSState, BSTransition> faBuilder = new FABuilder<>();

        // create the initial state
        BSState networkState = getBFANetworkState(bfaNetwork);

        // networkState.checkFinal();
        if (networkState.isFinal()) {
            faBuilder.putFinalState(networkState).putAcceptanceState(networkState);
        }

        // pass the initial state to the FABuilder
        faBuilder.putInitialState(networkState);

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
                    if (newState.isFinal()) {
                        faBuilder.putFinalState(newState).putAcceptanceState(newState);
                    }

                    if (!closed.contains(newState) && !toExplore.contains(newState)) {
                        toExplore.add(newState);
                        faBuilder.putState(newState);
                        faBuilder.putTransition(state, newState, new BSTransition(transition.getName(),
                                transition.getRelevanceLabel(), transition.getObservabilityLabel()));
                    } else {
                        BSState existent = closed.contains(newState)
                                ? closed.stream().filter(s -> s.equals(newState)).collect(MoreCollectors.onlyElement())
                                : toExplore.stream().filter(s -> s.equals(newState))
                                        .collect(MoreCollectors.onlyElement());
                        faBuilder.putTransition(state, existent, new BSTransition(transition.getName(),
                                transition.getRelevanceLabel(), transition.getObservabilityLabel()));
                    }
                    rollbackBFANetwork(state);
                }
            }

            toExplore.remove(state);
            closed.add(state);
        }

        rollbackBFANetwork(networkState);
        return faBuilder.build();
    }

    private static final LOBSState getBFANetworkLOBSState(BFANetwork bfaNetwork) {
        BSState networkState = getBFANetworkState(bfaNetwork);
        return new LOBSState(networkState.getName(), networkState.getBfas(), networkState.getLinks());
    }

    /**
     * Create the behavioral space related to a linear observation
     * 
     * @throws InvalidAlgorithmParameterException
     */
    public static final FA<LOBSState, BSTransition> getBehavioralSpaceForLinearObservation(BFANetwork bfaNetwork,
            List<String> linearObservation) throws InvalidAlgorithmParameterException {
        FABuilder<LOBSState, BSTransition> faBuilder = new FABuilder<>();
        // Construct the initial state of the behavioral space relative to
        // linearObservation
        LOBSState networkState = getBFANetworkLOBSState(bfaNetwork);
        networkState.setObservationIndex(0);
        faBuilder.putInitialState(networkState);

        // the set containing the BSStates still to be explored
        Set<LOBSState> toExplore = new HashSet<>();
        toExplore.add(networkState);

        Set<LOBSState> closed = new HashSet<>();

        while (!toExplore.isEmpty()) {
            LOBSState state = toExplore.iterator().next();
            rollbackBFANetwork(state);
            for (BFA bfa : bfaNetwork.getBFAs()) {
                for (EventTransition transition : getTransitionsEnabledInBfa(bfaNetwork, bfa)) {
                    if (transition.getObservabilityLabel().equals("")
                            || (state.getObservationIndex() < linearObservation.size() && linearObservation
                                    .get(state.getObservationIndex()).equals(transition.getObservabilityLabel()))) {

                        executeTransition(bfaNetwork, bfa, transition);
                        LOBSState newState = getBFANetworkLOBSState(bfaNetwork);

                        if (!transition.getObservabilityLabel().equals(""))
                            newState.setObservationIndex(state.getObservationIndex() + 1);
                        else
                            newState.setObservationIndex(state.getObservationIndex());

                        if (closed.contains(newState) || toExplore.contains(newState)) {
                            LOBSState existent = closed.contains(newState)
                                    ? closed.stream().filter(s -> s.equals(newState))
                                            .collect(MoreCollectors.onlyElement())
                                    : toExplore.stream().filter(s -> s.equals(newState))
                                            .collect(MoreCollectors.onlyElement());
                            faBuilder.putTransition(state, existent, new BSTransition(transition.getName(),
                                    transition.getRelevanceLabel(), transition.getObservabilityLabel()));
                        } else {
                            if (newState.getObservationIndex() == linearObservation.size() && newState.isFinal())
                                faBuilder.putFinalState(newState).putAcceptanceState(newState);

                            toExplore.add(newState);
                            faBuilder.putState(newState);
                            faBuilder.putTransition(state, newState, new BSTransition(transition.getName(),
                                    transition.getRelevanceLabel(), transition.getObservabilityLabel()));
                        }
                        rollbackBFANetwork(state);
                    }
                }

            }
            toExplore.remove(state);
            closed.add(state);

        }
        rollbackBFANetwork(networkState);

        FA<LOBSState, BSTransition> fa;
        try {
            fa = faBuilder.build();
        } catch (Exception e) {
            throw new InvalidAlgorithmParameterException();
        }
        return fa;
    }

    /**
     * Removes the states of the provided FA from which it can't be reached a final
     * state.
     *
     * @param fa  the Finite automata to be pruned
     * @param <S> the type of states (nodes) of the FA
     * @return true if at least one state has been removed, false otherwise
     */
    public static <S extends State> boolean pruneFA(FA<S, ?> fa) {
        Set<S> toRemove = new HashSet<>();
        for (S s : fa.getStates()) {
            Set<S> reachableNodes = reachableNodes(fa.getNetwork(), s); // get the set of nodes reachable from s
            // if (reachableNodes.stream().noneMatch(S::isFinal))
            if (Collections.disjoint(reachableNodes, fa.getFinalStates())) {
                toRemove.add(s);
            }
        }
        toRemove.forEach(s -> fa.getNetwork().removeNode(s));
        return toRemove.isEmpty() ? false : true;
    }

    /**
     * Returns the nodes in {@code network} that are reachable from {@code n}.
     */
    private static <N> Set<N> reachableNodes(Network<N, ?> network, N n) {
        checkArgument(network.nodes().contains(n), "Node %s is not an element of this network.", n);
        Queue<N> queue = new ArrayDeque<>();
        Set<N> visited = new HashSet<>();
        visited.add(n);
        queue.add(n);
        while (!queue.isEmpty()) {
            N current = queue.remove();
            for (N neighbor : network.successors(current)) {
                if (visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }
        return visited;
    }

    /**
     * Compute the silent closure of {@code state} relative to
     * {@code behavioralSpace}, by extracting the subspace of the nodes that are
     * reachable from {@code state} through non-observable transitions. States of
     * {@code behavioralSpace} that are final and exit states (those that has an
     * observable outgoing transition), are marked as acceptance states in the
     * returned FA.
     *
     * @param behavioralSpace the behavioral space to which {@code state} must
     *                        belong.
     * @param state           the state of {@code behavioralSpace} whose silent
     *                        closure we want to compute.
     * @return a FA corresponding to the silent closure of {@code state}
     */
    public static <S extends State> FA<S, BSTransition> silentClosure(FA<S, BSTransition> behavioralSpace, S state) {
        checkArgument(behavioralSpace.getStates().contains(state), "State %s does not belong to behavioral space %s",
                state, behavioralSpace);
        // check that state has at least one incoming observable transition
        checkArgument(
                behavioralSpace.getNetwork().inEdges(state).stream().anyMatch(BSTransition::hasObservabilityLabel)
                        || behavioralSpace.isInitial(state),
                "The provided state is neither initial, nor has any incoming observable transition");

        MutableNetwork<S, BSTransition> network = Graphs.copyOf(behavioralSpace.getNetwork());

        // collect and remove observable transitions
        Set<BSTransition> observableTransitions = network.edges().stream().filter(BSTransition::hasObservabilityLabel)
                .collect(Collectors.toSet());
        observableTransitions.forEach(t -> network.removeEdge(t));

        // find nodes reachable through non-observable transitions (since those
        // transitions have just been removed)
        Set<S> nodes = reachableNodes(network, state);

        // keep only transitions between nodes both reachable from state
        MutableNetwork<S, BSTransition> inducedSubgraph = Graphs.inducedSubgraph(network, nodes);

        // collect all final and exit states into acceptance states
        Set<S> finalStates = Sets.intersection(inducedSubgraph.nodes(), behavioralSpace.getFinalStates());
        Set<S> exitStates = inducedSubgraph.nodes().stream().filter(
                s -> behavioralSpace.getNetwork().outEdges(s).stream().anyMatch(BSTransition::hasObservabilityLabel))
                .collect(Collectors.toSet());

        Set<S> acceptanceStates = Sets.union(finalStates, exitStates);

        FA<S, BSTransition> silentClosure = new FA<>(state.getName(), inducedSubgraph, state, acceptanceStates,
                finalStates);
        return silentClosure;
    }

    /**
     * Compute the diagnosis associated to a silent closure, i.e. the alternative of
     * the decorations related to each final state of the closure.
     *
     * @return a map where keys are the final states of the closure and values are
     *         the corresponding decorations
     */
    public static FA<DBSState, BSTransition> decoratedSilentClosure(FA<BSState, BSTransition> silentClosure) {
        Map<BSState, String> acceptedLanguages = AcceptedLanguages.reduceFAtoMapOfRegex(silentClosure);
        MutableNetwork<BSState, BSTransition> network = silentClosure.getNetwork();

        FABuilder<DBSState, BSTransition> faBuilder = new FABuilder<>();
        faBuilder.name(silentClosure.getName());
        // temporary map needed for conversion from FA<BSState,...> to
        // FA<DecoratedBSState,...>
        Map<BSState, DBSState> states = new HashMap<>();

        for (BSState s : silentClosure.getStates()) {
            DBSState decState = new DBSState(s, acceptedLanguages.get(s));
            states.put(s, decState);
            if (silentClosure.isInitial(s))
                faBuilder.putInitialState(decState);
            if (silentClosure.isAcceptance(s))
                faBuilder.putAcceptanceState(decState);
            if (silentClosure.isFinal(s))
                faBuilder.putFinalState(decState);
        }

        for (BSTransition t : silentClosure.getTransitions()) {
            BSState stateU = network.incidentNodes(t).nodeU();
            BSState stateV = network.incidentNodes(t).nodeV();
            faBuilder.putTransition(states.get(stateU), states.get(stateV), t);
        }
        faBuilder.name(silentClosure.getName());
        FA<DBSState, BSTransition> decoratedSilentClosure = faBuilder.build();
        return decoratedSilentClosure;
    }

    /**
     * Compute the diagnosis associated to a silent closure, i.e. the alternative of
     * the decorations related to each final state of the closure.
     *
     * @return a map where keys are the final states of the closure and values are
     *         the corresponding decorations
     */
    public static Map<DBSState, String> diagnosis(FA<DBSState, BSTransition> decoratedSilentClosure) {
        Map<DBSState, String> diagnosis = new HashMap<>();
        for (DBSState s : decoratedSilentClosure.getFinalStates()) {
            diagnosis.put(s, s.getDecoration());
        }
        return diagnosis;
    }

    /**
     * Compute the decorated space of closures from a behavioral space
     *
     * @return a finite automata, whose nodes are silent closures
     */
    public static FA<FA<DBSState, BSTransition>, DSCTransition> decoratedSpaceOfClosures(
            FA<BSState, BSTransition> behavioralSpace) {
        // collect states that are valid entry states for a silent closure.
        Set<BSState> entryPoints = behavioralSpace.getStates().stream().filter(
                s -> behavioralSpace.getNetwork().inEdges(s).stream().anyMatch(BSTransition::hasObservabilityLabel))
                .collect(Collectors.toSet());
        entryPoints.add(behavioralSpace.getInitialState());

        // build a Map <EntryState, SilentClosure>
        Map<BSState, FA<DBSState, BSTransition>> decoratedSilentClosures = new HashMap<>();
        for (BSState s : entryPoints) {
            FA<BSState, BSTransition> silentClosure = BFANetworkSupervisor.silentClosure(behavioralSpace, s);
            decoratedSilentClosures.put(s, BFANetworkSupervisor.decoratedSilentClosure(silentClosure));
        }

        // build the decorated space of closures
        FABuilder<FA<DBSState, BSTransition>, DSCTransition> faBuilder = new FABuilder();
        for (FA<DBSState, BSTransition> sc1 : decoratedSilentClosures.values()) {
            // check if it is the initial silent closure
            if (behavioralSpace.isInitial(sc1.getInitialState().getBSState())) {
                faBuilder.putInitialState(sc1);
            }
            // check if it is an acceptance state (i.e. if the silent closure contains final
            // states)
            if (!sc1.getFinalStates().isEmpty()) {
                faBuilder.putAcceptanceState(sc1);
            }

            // find states of sc1 with observable outgoing transitions
            Set<DBSState> exitStates = sc1.getAcceptanceStates().stream().filter(s -> behavioralSpace.getNetwork()
                    .outEdges(s.getBSState()).stream().anyMatch(BSTransition::hasObservabilityLabel))
                    .collect(Collectors.toSet());

            // find observable transitions from each exit state of sc1 and build the space
            for (DBSState source : exitStates) {
                for (BSTransition t : behavioralSpace.getNetwork().outEdges(source.getBSState())) {
                    if (t.hasObservabilityLabel()) {
                        BSState target = behavioralSpace.getNetwork().incidentNodes(t).target();
                        FA<DBSState, BSTransition> sc2 = decoratedSilentClosures.get(target);
                        faBuilder.putTransition(sc1, sc2,
                                new DSCTransition(t.getName(),source.getDecoration() + t.getRelevanceLabel(), t.getObservabilityLabel()));
                    }
                }
            }
        }
        return faBuilder.build();
    }

    /**
     * Build the diagnostician given the decorated space of closures of a certain
     * behavioral space. The references to the silent closures (i.e. the nodes of
     * the space) are not kept, but they are instead replaced by simpler data
     * structures (states with just a name and a map representing the diagnosis).
     */
    public static Diagnostician diagnostician(FA<FA<DBSState, BSTransition>, DSCTransition> decoratedSpaceOfClosures) {
        FABuilder<FAState, DSCTransition> faBuilder = new FABuilder<>();
        Map<FAState, Map<DBSState, String>> diagnosis = new HashMap<>(); // map each state of the diagnostician to the
                                                                         // diagnosis (which is a map itself)

        Map<FA<DBSState, BSTransition>, FAState> states = new HashMap<>(); // temporary map needed for conversion from
                                                                           // silent closures to FAState
        for (FA<DBSState, BSTransition> silentClosure : decoratedSpaceOfClosures.getStates()) {
            // compute the diagnosis of the silent closure
            Map<DBSState, String> diagnosisOfS = diagnosis(silentClosure);
            FAState s = new FAState(silentClosure.getName());
            if (diagnosisOfS.size() > 0) {
                diagnosis.put(s, diagnosisOfS);
            }
            states.put(silentClosure, s);

            // add the state to the builder
            if (decoratedSpaceOfClosures.isInitial(silentClosure)) {
                faBuilder.putInitialState(s);
            }
            if (decoratedSpaceOfClosures.isAcceptance(silentClosure)) {
                faBuilder.putAcceptanceState(s);
            }
        }

        // add the transitions
        for (DSCTransition t : decoratedSpaceOfClosures.getTransitions()) {
            FA<DBSState, BSTransition> dsc1 = decoratedSpaceOfClosures.getNetwork().incidentNodes(t).source();
            FA<DBSState, BSTransition> dsc2 = decoratedSpaceOfClosures.getNetwork().incidentNodes(t).target();
            faBuilder.putTransition(states.get(dsc1), states.get(dsc2),
                    new DSCTransition(t.getName(), t.getSymbol(), t.getObservabilityLabel()));
        }

        FA<FAState, DSCTransition> fa = faBuilder.build();
        return new Diagnostician(fa, diagnosis);
    }

    private static String concatenateRegEx(String r1, String r2) {
        if (r1.equals(""))
            return r2;
        else if (r2.equals(""))
            return r1;
        if (r1.contains("|"))
            r1 = "(" + r1 + ")";
        if (r2.contains("|"))
            r2 = "(" + r2 + ")";
        return r1 + r2;
        /*
         * else if (r1.length() == 1 && r2.length() == 1) return r1 + r2; else if
         * (r1.length() == 1) return r1 + "(" + r2 + ")"; else if (r2.length() == 1)
         * return "(" + r1 + ")" + r2; return r1 + "(" + r2 + ")";
         */
    }

    private static String disjointRegEx(String r1, String r2) {
        if (r1.equals(r2))
            return r1;
        if (r1.contains("|"))
            r1 = "(" + r1 + ")";
        if (r2.contains("|"))
            r2 = "(" + r2 + ")";
        return r1 + "|" + r2;
        /*
         * else if (r1.length() == 1 && r2.length() == 1) return r1 + "|" + r2; else if
         * (r1.length() == 1 && r2.length() > 1) return r1 + "|(" + r2 + ")"; else if
         * (r2.length() == 1 && r1.length() > 1) return "(" + r1 + ")|" + r2; else if
         * (r1.equals("")) return "|(" + r2 + ")"; else if (r2.equals("")) return "(" +
         * r1 + ")|";
         * 
         * return "(" + r1 + ")|(" + r2 + ")";
         */
    }

    /**
     * Computes the linear diagnosis relating to the linear observation @code
     * linObs} of a behavioral network, given its diagnostician
     * {@code diagnostician}, using algorithm of page 85 of the project description.
     * 
     * @return A string representing the diagnosis of the provided linear
     *         observation
     * @throws InvalidAlgorithmParameterException
     */
    public static String linearDiagnosis(Diagnostician diagnostician, List<String> linObs)
            throws InvalidAlgorithmParameterException {
        FA<FAState, DSCTransition> fa = diagnostician.getFa();
        FAState x0 = fa.getInitialState();
        Map<FAState, String> X = new HashMap<>();
        X.put(x0, EPS);
        for (String o : linObs) {
            Map<FAState, String> Xnew = new HashMap<>();
            for (FAState x1 : X.keySet()) {
                String r1 = X.get(x1);
                // get observable transitions from x1
                Set<DSCTransition> outTransitions = fa.getNetwork().outEdges(x1).stream()
                        .filter(t -> t.getObservabilityLabel().equals(o)).collect(Collectors.toSet());
                for (DSCTransition t : outTransitions) {
                    FAState x2 = fa.getNetwork().incidentNodes(t).target();
                    String r2 = concatenateRegEx(r1, t.getSymbol());
                    // String r2 = "(" + r1 + ")(" + t.getSymbol() + ")"; // line 6 of algorithm of
                    // page 85
                    if (Xnew.containsKey(x2)) {
                        r2 = disjointRegEx(Xnew.get(x2), r2);
                        // r2 = "(" + Xnew.get(x2) + ")|(" + r2 + ")";
                        Xnew.replace(x2, r2);
                    } else {
                        Xnew.put(x2, r2);
                    }
                }
            }
            X = Xnew;
        }

        Set<FAState> nonAcceptanceStates = X.keySet().stream().filter(x -> !fa.isAcceptance(x))
                .collect(Collectors.toSet());
        X.keySet().removeAll(nonAcceptanceStates);
        if (X.isEmpty()) {
            throw new InvalidAlgorithmParameterException();
        }

        StringBuilder sb = new StringBuilder();

        for (FAState x : X.keySet()) {
            sb.append("(" + X.get(x) + ")(" + diagnostician.getDiagnosisOf(x) + ")|");
        }
        sb.setLength(sb.length() - 1);
        String res = sb.toString().replaceAll("null", "");
        return res;

    }

}