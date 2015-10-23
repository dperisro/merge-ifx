package com.bs.ifx.merge.services;

import com.bs.ifx.merge.conf.MergeConfig;
import com.bs.ifx.merge.entities.MergeEntity;
import com.bs.ifx.merge.entities.MergeFileFilter;
import com.bs.ifx.merge.entities.MergeRef;
import com.bs.ifx.merge.util.MergeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class MergeService extends MergeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeService.class);
    private static Map<String, MergeEntity> mapNodes = new HashMap<String, MergeEntity>();
    private static Map<String, Node> commonNodes = new HashMap<String, Node>();

    //TEST Elimiar
    private static Set<String> commonNodesTest = new HashSet<String>();

    @Autowired
    private MergeConfig config;

    @PostConstruct
    public void init() throws Exception {
        LOGGER.info(config.toString());
        prepareInput(config.getInputPath());
        prepareOutput(config.getOutputPath());
    }

    public void doMerge() throws Exception {

        String[] filesXSD = new File(config.getInputPath()).list(new MergeFileFilter());
        for (String keyWord : config.getKeys()) {
            for (String inputFile : filesXSD) {
                prepareMerge(keyWord, new File(config.getInputPath(), inputFile));
            }
        }

        LOGGER.info("mapNodes: " + mapNodes.toString());
        LOGGER.info("commonNodesTest: " + commonNodesTest.toString());

        for (String keyWord : config.getKeys()) {
            Document messageFile = this.createSkeletonFile();
            createXSDInclude(messageFile, mapNodes.get(keyWord));
            for (Node imported : mapNodes.get(keyWord).getNodeMatch()) {
                Node imported2 = messageFile.importNode(imported, true);
                messageFile.getDocumentElement().appendChild(imported2);
            }
            writeNode(config.getOutputPath(), keyWord + ".xsd", messageFile);
        }

        Document commonIFX = createSkeletonFile();
        for (String commonElement : commonNodes.keySet()) {
            Node imported2 = commonIFX.importNode(commonNodes.get(commonElement), true);
            commonIFX.getDocumentElement().appendChild(imported2);
        }
        writeNode(config.getOutputPath(), MergeConfig.COMMON_XSD + ".xsd", commonIFX);
    }

    public void prepareMerge(final String key, final File file) throws Exception {
        LOGGER.info("Key: " + key + " && File: " + file.getAbsolutePath());
        MergeEntity entity = new MergeEntity(key);
        Document sourceDoc = getDocumentBuilder().parse(file);
        NodeList sourceDocChildNodes = sourceDoc.getChildNodes();

        for (int i = 0; i < sourceDocChildNodes.getLength(); i++) {
            NodeList childNodes2 = sourceDocChildNodes.item(i).getChildNodes();
            for (int j = 0; j < childNodes2.getLength(); j++) {
                NamedNodeMap attributes = childNodes2.item(j).getAttributes();
                if (attributes != null && attributes.getNamedItem("name") != null) {
                    Node node = childNodes2.item(j).getAttributes().getNamedItem("name");
                    Node clone = childNodes2.item(j).cloneNode(true);
                    MergeRef refOtherKey = isMatchingNodeOtherKeys(node, key, config.getKeys());
                    if (isMatchingNode(node, key)) {
                        entity.getNodeMatch().add(clone);
                        entity.getNodeTestString().add(node.getNodeValue());
                    } else if (refOtherKey.isRef()) {
                        entity.getKeysMatching().add(refOtherKey.getName());
                    } else {
                        commonNodes.put(node.getNodeValue(), clone);
                    }
                }
            }
        }
        mapNodes.put(key, entity);
    }


}