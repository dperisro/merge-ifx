package com.bs.ifx.merge.services;

import com.bs.ifx.merge.conf.MergeConfig;
import com.bs.ifx.merge.entities.MergeEntity;
import com.bs.ifx.merge.entities.MergeFileFilter;
import com.bs.ifx.merge.entities.MergeNode;
import com.bs.ifx.merge.util.MergeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class MergeService2 extends MergeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeService2.class);

    private String[] filesXSD;
    private static Map<String, MergeEntity> mapNodes = new LinkedHashMap<>();
    private static Map<String, Set<String>> subNodes = new LinkedHashMap<>();

    @Autowired
    private MergeConfig config;

    @PostConstruct
    private void init() throws Exception {
        LOGGER.info(config.toString());
        prepareInput(config.getInputPath());
        prepareOutput(config.getOutputPath());
        filesXSD = new File(config.getInputPath()).list(new MergeFileFilter());
        for (String keyWord : config.getKeys()) {
            mapNodes.put(keyWord, new MergeEntity(keyWord));
        }
        mapNodes.put(MergeConfig.DATATYPE_XSD, new MergeEntity(MergeConfig.DATATYPE_XSD));
        mapNodes.put(MergeConfig.COMMON_XSD, new MergeEntity(MergeConfig.COMMON_XSD));
    }

    public void doMerge() throws Exception {

        Map<String, Set<String>> mapKeys = new LinkedHashMap<>();

        for (String inputFile : filesXSD) {
            prepareByFile(new File(config.getInputPath(), inputFile));
        }
        LOGGER.info("mapNodes: " + mapNodes.toString());
        LOGGER.info("subNodes: " + subNodes.toString());

        for (String keyWord : mapNodes.keySet()) {
            MergeEntity entity = mapNodes.get(keyWord);
            createFile(keyWord, entity, config.getOutputPath(), subNodes, config.getKeys());
        }

    }

    private void prepareByFile(final File file) throws Exception {
        NodeList sourceDocChildNodes = getDocumentBuilder(file);
        for (int i = 0; i < sourceDocChildNodes.getLength(); i++) {
            NodeList childNodes2 = sourceDocChildNodes.item(i).getChildNodes();
            for (int j = 0; j < childNodes2.getLength(); j++) {
                NamedNodeMap attributes = childNodes2.item(j).getAttributes();
                if (attributes != null && attributes.getNamedItem("name") != null) {
                    Node node = childNodes2.item(j).getAttributes().getNamedItem("name");
                    Node clone = childNodes2.item(j).cloneNode(true);
                    MergeNode mergeNode = new MergeNode(clone, node.getNodeValue());
                    String keyMatch = getKeyMatchingNode(node, config.getKeys());
                    subNodes.put(node.getNodeValue(), depthKeyMatchingNode(node));
                    if (isMatchingNodeBase(node, config.getBase())) {
                        mapNodes.get(MergeConfig.DATATYPE_XSD).addNode(node.getNodeValue(), mergeNode, null);
                    } else if (keyMatch != null) {
                        mapNodes.get(keyMatch).addNode(node.getNodeValue(), mergeNode, null);
                    } else {
                        mapNodes.get(MergeConfig.COMMON_XSD).addNode(node.getNodeValue(), mergeNode, null);
                    }
                }
            }
        }
    }


}