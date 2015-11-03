package com.bs.ifx.merge.entities;

import com.bs.ifx.merge.conf.MergeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.*;

public class MergeEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeEntity.class);

    private String key;

    private HashMap<String, MergeNode> nodeMatch;
    private Set<String> keysMatch;
    private Set<String> nodeMatchString;

    public MergeEntity(final String keyV) {
        this.key = keyV;
        this.nodeMatch = new LinkedHashMap<String, MergeNode>();
        this.nodeMatchString = new LinkedHashSet<>();
        if (keyV.equalsIgnoreCase(MergeConfig.DATATYPE_XSD)) {
            this.keysMatch = new HashSet<String>();
        } else if (keyV.equalsIgnoreCase(MergeConfig.COMMON_XSD)) {
            this.keysMatch = new HashSet<String>(Arrays.asList(MergeConfig.DATATYPE_XSD));
        } else {
            this.keysMatch = new HashSet<String>(Arrays.asList(MergeConfig.DATATYPE_XSD, MergeConfig.COMMON_XSD));
        }
    }

    public String getKey() {
        return key;
    }

    public HashMap<String, MergeNode> getNodeMatch() {
        return nodeMatch;
    }

    public Set<String> getKeysMatch() {
        return keysMatch;
    }

    public Set<String> getNodeMatchString() {
        return nodeMatchString;
    }

    public void addNode(final String nodeValue, final MergeNode clone, Set<String> bOpt) {
        if (!getNodeMatch().containsKey(nodeValue)) {
            getNodeMatch().put(nodeValue, clone);
            getNodeMatchString().add(nodeValue);
            if (bOpt != null) {
                getKeysMatch().addAll(bOpt);
            }
        }
    }

    private MergeRef hasKeydepth(Node clone) {
        return new MergeRef("key");
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
