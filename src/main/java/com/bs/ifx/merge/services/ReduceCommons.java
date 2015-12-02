package com.bs.ifx.merge.services;

import com.bs.ifx.merge.conf.MergeConfig;
import com.bs.ifx.merge.entities.MergeFileFilter;
import com.bs.ifx.merge.util.MergeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.*;

@Component
public class ReduceCommons extends MergeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReduceCommons.class);

    private String[] filesXSD;
    private static Map<String, List<String>> mapNodes = new LinkedHashMap<>();
    private static Map<String, List<String>> mapNodesPrefix = new LinkedHashMap<>();
    private static Map<String, Integer> mapFileNumber = new LinkedHashMap<>();

    private static Map<String, Integer> mapIdentNode = new LinkedHashMap<>();

    private static List<String> tree = new LinkedList<>();

    private int index = 0;

    @Autowired
    private MergeConfig config;

    public void doAnalysis() throws Exception {
        init();
    }

    synchronized public int getIndex() {
        return index++;
    }

    private void init() throws Exception {
        LOGGER.info(config.toString());

        for (String keyBase : config.getKeys()) {
            mapNodes.put(keyBase, new LinkedList<>());
            mapNodesPrefix.put(keyBase, new LinkedList<>());
        }

        filesXSD = new File(config.getOutputPath() + "/" + MergeConfig.PATH_NONS).list(new MergeFileFilter());
        for (String inputFile : filesXSD) {
            mapFileNumber.put(inputFile, 0);
            tunning(new File(config.getOutputPath(), MergeConfig.PATH_NONS + "/" + inputFile));
        }
        LOGGER.info("mapFileNumber: " + mapFileNumber.toString());

        for (String keyBase : mapNodes.keySet()) {
            LOGGER.info(keyBase + "=" + mapNodes.get(keyBase));
        }

        LOGGER.info("tree: " + tree.toString());
    }

    public void iterateElementsReduce(final NodeList list) {
        Set<String> depthKeys = new HashSet<>();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i) instanceof Element) {
                Element first = (Element) list.item(i);
                if (first.hasAttributes()) {
                    String valueType = first.getAttribute("type");
                    String valueRef = first.getAttribute("ref");
                    if (StringUtils.isNotBlank(valueType)) {
                        depthKeys.add(valueType);
                    }
                    if (StringUtils.isNotBlank(valueRef)) {
                        depthKeys.add(valueRef);
                    }
                }

                if (first.hasChildNodes()) {
                    NodeList list2 = first.getElementsByTagName("*");
                    iterateElements(list2);
                }
            }
        }
    }

    private void tunning(File file) throws Exception {
        LOGGER.info(file.getName());

        mapIdentNode.put(file.getName(), index++);

        NodeList sourceDocChildNodes = getDocumentBuilder(file);
        for (int i = 0; i < sourceDocChildNodes.getLength(); i++) {
            NodeList childNodes2 = sourceDocChildNodes.item(i).getChildNodes();
            for (int j = 0; j < childNodes2.getLength(); j++) {
                NamedNodeMap attributes = childNodes2.item(j).getAttributes();
                if (attributes != null && attributes.getNamedItem("name") != null) {
                    Node node = childNodes2.item(j).getAttributes().getNamedItem("name");
                    mapIdentNode.put(node.getNodeValue(), index++);

                    mapFileNumber.put(file.getName(), mapFileNumber.get(file.getName()) + 1);
                    tree.add(i++ + "," + node.getNodeValue() + "," + file.getName());
                    for (String keyBase : config.getKeys()) {
                        if (containsKeys(node.getNodeValue(), keyBase)) {
                            mapNodes.get(keyBase).add(node.getNodeValue());
                        }
                    }
                }
            }
        }
    }

    public boolean containsKeys(final String value, final String key) {
        if (value.contains(key)) {
            return true;
        }
        return false;
    }

}
