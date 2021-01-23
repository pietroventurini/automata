package files;

import com.google.common.graph.EndpointPair;
import graph.BFAnetwork.BFANetwork;
import graph.BFAnetwork.BFANetworkBuilder;
import graph.BFAnetwork.Link;
import graph.bfa.BFA;

import java.util.*;

class BFANetworkJson {

    BFAJson[] bfas;
    LinkJson[] links;

    BFANetworkJson(BFAJson[] bfas, LinkJson[] links) {
        this.bfas = bfas;
        this.links = links;
    }

    /**
     * Alias of the other constructor but it does the unpacking
     */
    BFANetworkJson(BFANetwork bfaNetwork) {
        this(bfasToArray(bfaNetwork), linksToArray(bfaNetwork));
    }

    /**
     * Given BFANetwork, returns an array of LinkJson of the form:
     *  [
     *      {"name":"transition.name", "source":"source.name", "target":"target.name"},
     *      ...
     *      {"name":"transition.name", "source":"source.name", "target":"target.name"}
     *  ]
     */
    static final LinkJson[] linksToArray(BFANetwork bfaNet) {
        Set<Link> links = bfaNet.getLinks();
        List<LinkJson> list = new ArrayList<>();
        for (Link l : links) {
            EndpointPair<BFA> incidentNodes = bfaNet.getNetwork().incidentNodes(l);
            String source = incidentNodes.source().getName();
            String target = incidentNodes.target().getName();
            LinkJson jsonLink = new LinkJson(l.getName(), source, target);
            list.add(jsonLink);
        }
        return list.toArray(LinkJson[]::new);
    }

    /**
     * Given BFANetwork, returns an array of BFAJson
     */
    static final BFAJson[] bfasToArray(BFANetwork bfaNet) {
        Set<BFA> bfas = bfaNet.getBFAs();
        return bfas.stream().map(BFAJson::new).toArray(BFAJson[]::new);
    }


    /**
     * Converts the BFANetworkJson into a BFANetwork by invoking its builder.
     */
    BFANetwork toBFANetwork() {
        BFANetworkBuilder builder = new BFANetworkBuilder();

        // map bfaName to BFA
        Map<String, BFA> namesToBfas = new HashMap<>();
        for (BFAJson bfa : bfas) {
            namesToBfas.putIfAbsent(bfa.name, bfa.toBFA());
        }

        // add the network nodes
        for (BFA bfa : namesToBfas.values()) {
            builder.putBFA(bfa);
        }

        // add the network links
        for (LinkJson l : links) {
            builder.putLink(namesToBfas.get(l.source), namesToBfas.get(l.target), l.toLink());
        }

        return builder.build();
    }
}
