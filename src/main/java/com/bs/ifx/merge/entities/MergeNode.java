package com.bs.ifx.merge.entities;

import org.w3c.dom.Node;

public class MergeNode {

    private String name;
    private Node node;

    public MergeNode(final Node nodeV, final String nameV) {
        this.node = nodeV;
        this.name = nameV;
    }

    public Node getNode() {
        return node;
    }

    public String getName() {
        return name;
    }

}
