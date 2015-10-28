package com.bs.ifx.merge.entities;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MergeEntity {

    private String key;
    private List<Node> nodeMatch;
    private Set<String> keysMatch;
    private List<String> nodeMatchString;

    public MergeEntity(final String keyV) {
        this.key = keyV;
        this.nodeMatch = new ArrayList<Node>();
        this.keysMatch = new HashSet<String>();
        this.nodeMatchString = new ArrayList<String>();

    }

    public String getKey() {
        return key;
    }

    public List<Node> getNodeMatch() {
        return nodeMatch;
    }

    public Set<String> getKeysMatch() {
        return keysMatch;
    }

    public List<String> getNodeMatchString() {
        return nodeMatchString;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        result.append(this.getClass().getName() + " {" + newLine);
        result.append(" key: " + getKey() + newLine);
        result.append(" nodeMatch: " + getNodeMatchString() + newLine);
        result.append(" keysMatch: " + getKeysMatch() + newLine);
        result.append("}");
        return result.toString();
    }
}
