package com.bs.ifx.merge.entities;

import com.bs.ifx.merge.conf.MergeConfig;
import org.w3c.dom.Node;

import java.util.*;

public class MergeEntity {

    private String key;
    private HashMap<String, Node> nodeMatch;
    private Set<String> keysMatch;
    private Set<String> nodeMatchString;

    public MergeEntity(final String keyV) {
        this.key = keyV;
        this.nodeMatch = new LinkedHashMap<>();
        this.nodeMatchString = new LinkedHashSet<>();
        if (keyV.equalsIgnoreCase(MergeConfig.DATATYPE_XSD)) {
            this.keysMatch = new HashSet<>();
        } else if (keyV.equalsIgnoreCase(MergeConfig.COMMON_XSD)) {
            this.keysMatch = new HashSet<>(Arrays.asList(MergeConfig.DATATYPE_XSD, MergeConfig.DATETIME_XSD,
                    MergeConfig.ADDRTYPE_XSD, MergeConfig.CODETYPE_XSD));
        } else if (keyV.equalsIgnoreCase(MergeConfig.DATETIME_XSD)) {
            this.keysMatch = new HashSet<>(Arrays.asList(MergeConfig.DATATYPE_XSD));
        } else if (keyV.equalsIgnoreCase(MergeConfig.ADDRTYPE_XSD)) {
            this.keysMatch = new HashSet<>(Arrays.asList(MergeConfig.DATATYPE_XSD));
        } else if (keyV.equalsIgnoreCase(MergeConfig.CODETYPE_XSD)) {
            this.keysMatch = new HashSet<>(Arrays.asList(MergeConfig.DATATYPE_XSD));
        } else {
            this.keysMatch = new HashSet<>(Arrays.asList(MergeConfig.DATATYPE_XSD, MergeConfig.COMMON_XSD,
                    MergeConfig.HEADERS_XSD, MergeConfig.DATETIME_XSD, MergeConfig.ADDRTYPE_XSD, MergeConfig.CODETYPE_XSD));
        }
    }

    private String getKey() {
        return key;
    }

    public HashMap<String, Node> getNodeMatch() {
        return nodeMatch;
    }

    public Set<String> getKeysMatch() {
        return keysMatch;
    }

    public boolean hasEmptyNodes() {
        return getNodeMatch().isEmpty();
    }

    public void addNode(final String nodeValue, final Node clone, final Set<String> bOpt) {
        if (!getNodeMatch().containsKey(nodeValue)) {
            getNodeMatch().put(nodeValue, clone);
            nodeMatchString.add(nodeValue);
            if (bOpt != null) {
                getKeysMatch().addAll(bOpt);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        result.append(this.getClass().getName() + " {" + newLine);
        result.append(" key: " + getKey() + newLine);
        result.append(" nodeMatch: " + nodeMatchString + newLine);
        result.append(" keysMatch: " + getKeysMatch() + newLine);
        result.append("}");
        return result.toString();
    }
}
