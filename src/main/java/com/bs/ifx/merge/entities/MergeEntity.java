package com.bs.ifx.merge.entities;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MergeEntity {

    private String key;
    private List<Node> nodeMatch;
    private Set<String> keysMatching;
    private List<String> nodeTestString = new ArrayList<String>();

    public MergeEntity(final String keyV) {
        this.key = keyV;
        nodeMatch = new ArrayList<Node>();
        keysMatching = new HashSet<String>();
    }

    public String getKey() {
        return key;
    }

    public List<Node> getNodeMatch() {
        return nodeMatch;
    }

    public Set<String> getKeysMatching() {
        return keysMatching;
    }

    public List<String> getNodeTestString() {
        return nodeTestString;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        result.append(this.getClass().getName() + " {" + newLine);
        result.append(" key: " + getKey() + newLine);
        result.append(" nodeMatch: " + getNodeMatch() + newLine);
        result.append(" nodeTestString: " + getNodeTestString() + newLine);
        result.append(" getKeysMatching: " + getKeysMatching() + newLine);
        result.append("}");
        return result.toString();
    }
}
