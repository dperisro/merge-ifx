package com.bs.ifx.merge.services;

import com.bs.ifx.merge.conf.MergeConfig;
import com.bs.ifx.merge.util.MergeFileFilter;
import com.bs.ifx.merge.util.MergeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;

@Component
public class MergeService extends MergeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeService.class);
    private static Map<String, List<Node>> mapNodes = new HashMap<String, List<Node>>();
    private static Set<Node> commonNodes = new HashSet<Node>();

    //TEST Elimiar
    private static Map<String, List<String>> mapNodesTest = new HashMap<String, List<String>>();
    private static Set<String> commonNodesTest = new HashSet<String>();

    private Document commonIFX;

    @Autowired
    private MergeConfig config;

    @PostConstruct
    public void init() throws Exception {
        LOGGER.info(config.toString());
        prepareInput(config.getInputPath());
        prepareOutput(config.getOutputPath());
        commonIFX = createSkeletonFile();
        mapNodes.clear();
    }

    //writeNode(config.getOutputPath(), MergeConfig.COMMON_XSD, commonIFX);


    public void merge() throws Exception {

        Document commonIFX = createSkeletonFile();
        writeNode(config.getOutputPath(), MergeConfig.COMMON_XSD, commonIFX);

        String[] filesXSD = new File(config.getInputPath()).list(new MergeFileFilter());
        for (String keyWord : config.getKeys()) {
            for (String inputFile : filesXSD) {
                doSomething(keyWord, new File(config.getInputPath(), inputFile), commonIFX);
            }
        }

        for (String keyWord : config.getKeys()) {
            Document messageFile = this.createSkeletonFile();
            Element createElementNS = messageFile.createElement("xsd:include");
            createElementNS.setAttribute("schemaLocation", MergeConfig.COMMON_XSD);
            Node importNode = messageFile.importNode(createElementNS, true);
            messageFile.getDocumentElement().appendChild(importNode);
            for (Node imported : mapNodes.get(keyWord)) {
                imported = messageFile.importNode(imported, true);
                messageFile.getDocumentElement().appendChild(imported);
            }
            writeNode(config.getOutputPath(), keyWord + ".xsd", messageFile);
        }

        LOGGER.info(mapNodesTest.toString());
        LOGGER.info("commonNodesTest: " + commonNodesTest.toString());


    }

    public void doSomething(String key, File file, Document commonIFX) throws Exception {
        LOGGER.info("Key: " + key + " && File: " + file.getAbsolutePath());

        List<Node> nodes = new ArrayList<Node>();
        List<String> nodeString = new ArrayList<String>();


        Document sourceDoc = getDocumentBuilder().parse(file);
        NodeList sourceDocChildNodes = sourceDoc.getChildNodes();

        for (int i = 0; i < sourceDocChildNodes.getLength(); i++) {
            NodeList childNodes2 = sourceDocChildNodes.item(i).getChildNodes();
            for (int j = 0; j < childNodes2.getLength(); j++) {
                NamedNodeMap attributes = childNodes2.item(j).getAttributes();
                if (attributes != null && attributes.getNamedItem("name") != null) {
                    Node node = childNodes2.item(j).getAttributes().getNamedItem("name");
                    Node clone = childNodes2.item(j).cloneNode(true);
                    if (node.getNodeValue().startsWith(key)) {
                        LOGGER.info(clone.getNodeValue());
                        nodes.add(clone);
                        nodeString.add(node.getNodeValue());
                    } else {
                        commonNodes.add(clone);
                        commonNodesTest.add(node.getNodeValue());
                    }
                }
            }
        }

        mapNodes.put(key, nodes);
        mapNodesTest.put(key, nodeString);

    }


}