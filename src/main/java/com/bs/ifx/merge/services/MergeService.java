package com.bs.ifx.merge.services;

import com.bs.ifx.merge.conf.MergeConfig;
import com.bs.ifx.merge.entities.MergeEntity;
import com.bs.ifx.merge.entities.MergeFileFilter;
import com.bs.ifx.merge.entities.MergeRef;
import com.bs.ifx.merge.util.MergeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Component
public class MergeService extends MergeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeService.class);

    private String[] filesXSD;
    private static Map<String, MergeEntity> mapNodes = new LinkedHashMap<>();
    private static Map<String, String> mapNodesKey = new LinkedHashMap<>();

    @Autowired
    private MergeConfig config;

    private void init() throws Exception {
        LOGGER.info(config.toString());
        prepareInput(config.getInputPath());
        prepareOutput(config.getOutputPath());
        filesXSD = new File(config.getInputPath()).list(new MergeFileFilter());
        for (String keyWord : config.getKeys()) {
            mapNodes.put(keyWord, new MergeEntity(keyWord));
        }
        mapNodes.put(MergeConfig.DATATYPE_XSD, new MergeEntity(MergeConfig.DATATYPE_XSD));
        mapNodes.put(MergeConfig.DATETIME_XSD, new MergeEntity(MergeConfig.DATETIME_XSD));
        mapNodes.put(MergeConfig.CODETYPE_XSD, new MergeEntity(MergeConfig.CODETYPE_XSD));
        mapNodes.put(MergeConfig.ADDRTYPE_XSD, new MergeEntity(MergeConfig.ADDRTYPE_XSD));
        mapNodes.put(MergeConfig.COMMON_XSD, new MergeEntity(MergeConfig.COMMON_XSD));
        mapNodes.put(MergeConfig.HEADERS_XSD, new MergeEntity(MergeConfig.HEADERS_XSD));
    }

    public void doMerge() throws Exception {
        init();
        for (String inputFile : filesXSD) {
            prepareByFile(new File(config.getInputPath(), inputFile));
        }
        for (String keyWord : mapNodes.keySet()) {
            if (!keyWord.equals(MergeConfig.COMMON_XSD)) {
                prepareSubKeys(keyWord);
            }
            MergeEntity entity = mapNodes.get(keyWord);
            createFile(keyWord, entity, config.getOutputPath(), true);
        }
        LOGGER.info("mapNodes: " + mapNodes.toString());
        prepareNameSpaces();
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
                    String keyMatch = getKeyMatchingNode(node, config.getKeys());
                    String keyException = getKeysByValue(config.getExceptions(), node.getNodeValue());
                    String addKey = MergeConfig.COMMON_XSD;
                    if (isMatchingNodeBase(node, config.getBase())) {
                        addKey = MergeConfig.DATATYPE_XSD;
                    } else if (StringUtils.isNotBlank(keyException)) {
                        addKey = keyException;
                    } else if (isMatchingDateTime(node)) {
                        addKey = MergeConfig.DATETIME_XSD;
                    } else if (isMatchingCode(node)) {
                        addKey = MergeConfig.CODETYPE_XSD;
                    } else if (isMatchingAdress(node)) {
                        addKey = MergeConfig.ADDRTYPE_XSD;
                    } else if (keyMatch != null) {
                        addKey = keyMatch;
                    }
                    mapNodes.get(addKey).addNode(node.getNodeValue(), clone, null);
                    mapNodesKey.put(node.getNodeValue(), getPrefixByKey(addKey));
                }
            }
        }
    }

    private void prepareSubKeys(final String keyWord) throws Exception {
        Set<String> subKeys = new LinkedHashSet<>();
        for (Node node : mapNodes.get(keyWord).getNodeMatch().values()) {
            Set<String> depthKeys = iterateElements(node.getChildNodes());
            for (String subNode : depthKeys) {
                MergeRef mergeRef = isMatchNodeWithKeys(subNode, keyWord, config.getKeys());
                if (mergeRef.isRef() && !mapNodes.get(mergeRef.getName()).hasEmptyNodes()) {
                    subKeys.add(mergeRef.getName());
                }
            }
        }
        mapNodes.get(keyWord).getKeysMatch().addAll(subKeys);
    }

    private void prepareNameSpaces() throws Exception {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        for (String keyWord : mapNodes.keySet()) {
            File test = prepareInputFileByKey(config.getOutputPath(), keyWord);
            LOGGER.debug("keyTemp: " + keyWord + ", " + test.exists() + "");
            Document doc = docBuilder.parse(test);
            if (!keyWord.equals(MergeConfig.DATATYPE_XSD)) {
                NodeList list = doc.getElementsByTagName("*");
                prepareNameSpacesByNode(list, mapNodesKey);
            }
            writeNode(config.getOutputPath(), keyWord, doc, false);
        }
    }
}