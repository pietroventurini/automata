package files;

import graph.BFAnetwork.Link;

class LinkJson {
    String name;
    String source;
    String target;

    LinkJson(String name, String source, String target) {
        this.name = name;
        this.source = source;
        this.target = target;
    }


    /**
     * Converts the EventTransitionJson into an EventTransition by invoking its builder.
     */
    Link toLink() {
        return new Link(name);
    }
}
