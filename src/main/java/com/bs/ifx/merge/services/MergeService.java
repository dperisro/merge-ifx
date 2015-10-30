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
import org.w3c.dom.*;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

@Component
public class MergeService extends MergeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeService.class);

    private String[] filesXSD;
    private static Map<String, MergeEntity> mapNodes = new HashMap<String, MergeEntity>();
    private static Map<String, Node> commonNodes = new HashMap<String, Node>();
    private static Set<String> commonNodesString = new HashSet<String>();

    @Autowired
    private MergeConfig config;

    @PostConstruct
    private void init() throws Exception {
        LOGGER.info(config.toString());
        prepareInput(config.getInputPath());
        prepareOutput(config.getOutputPath());
        filesXSD = new File(config.getInputPath()).list(new MergeFileFilter());
    }

    public void doMerge() throws Exception {
        prepareBase();
        prepareMerge();
        prepareCommons();
        testNS();
    }

    private void testElement(final NodeList list, final String type) {
        //loop to print data
        for (int i = 0; i < list.getLength(); i++) {
            Element first = (Element) list.item(i);
            if (first.hasAttributes()) {


                if (type.equals("type")) {
                    String nm = first.getAttribute("name");
                    String nm1 = first.getAttribute("type");
                    if (config.getBase().contains(nm1)) {
                        String value = "bas:" + first.getAttributeNode("type").getNodeValue();
                        first.getAttributeNode("type").setValue(value);
                        LOGGER.info("ELEMENT_NODE 2: " + nm + "-->" + nm1);
                    }
                } else {
                    String nm = first.getAttribute("name");
                    String nm1 = first.getAttribute("base");

                    String value = "bas:" + first.getAttributeNode("base").getNodeValue();
                    first.getAttributeNode("base").setValue(value);


                    LOGGER.info("ELEMENT_NODE 2: " + nm + "-->" + nm1);

                }
                if (first.hasChildNodes()) {
                    LOGGER.info("ELEMENT_NODE");
                    //testElement(first.getChildNodes());
                }
            }
        }
    }

    private void testNS() throws Exception {
        File test = new File("dist/common.xsd");
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.parse(test);
        NodeList list = doc.getElementsByTagName("xsd:element");
        testElement(list, "type");

        NodeList list2 = doc.getElementsByTagName("xsd:restriction");
        testElement(list2, "rest");


        writeNode(config.getOutputPath(), MergeConfig.COMMON_XSD + "2" + MergeConfig.EXT_XSD, doc);
    }

    private void prepareBase() throws Exception {
        for (String inputFile : filesXSD) {
            prepareBaseByFile(new File(config.getInputPath(), inputFile));
        }
    }

    private void prepareBaseByFile(final File file) throws Exception {
        MergeEntity entity = new MergeEntity(MergeConfig.DATATYPE_XSD);
        NodeList sourceDocChildNodes = getDocumentBuilder(file);

        for (int i = 0; i < sourceDocChildNodes.getLength(); i++) {
            NodeList childNodes2 = sourceDocChildNodes.item(i).getChildNodes();
            for (int j = 0; j < childNodes2.getLength(); j++) {
                NamedNodeMap attributes = childNodes2.item(j).getAttributes();
                if (attributes != null && attributes.getNamedItem("name") != null) {
                    Node node = childNodes2.item(j).getAttributes().getNamedItem("name");
                    Node clone = childNodes2.item(j).cloneNode(true);
                    /*if (isMatchingNodeBase(node, config.getBase())) {
                        //entity.getNodeMatch().ad
                        //entity.getNodeMatchString().add(node.getNodeValue());
                    }*/
                }
            }
        }
        mapNodes.put(MergeConfig.DATATYPE_XSD, entity);
    }

    private void prepareCommons() throws Exception {
        Set<String> h = new HashSet<String>(Arrays.asList("base"));
        Document commonIFX = createSkeletonFile(MergeConfig.COMMON_XSD, h);
        createXSDInclude(commonIFX, MergeConfig.DATATYPE_XSD);
        for (String commonElement : commonNodes.keySet()) {
            Node imported2 = commonIFX.importNode(commonNodes.get(commonElement), true);
            commonIFX.getDocumentElement().appendChild(imported2);
        }
        writeNode(config.getOutputPath(), MergeConfig.COMMON_XSD + MergeConfig.EXT_XSD, commonIFX);
    }

    private void prepareMerge() throws Exception {
        for (String keyWord : config.getKeys()) {
            for (String inputFile : filesXSD) {
                prepareMergeKeyFile(keyWord, new File(config.getInputPath(), inputFile));
            }
        }
        LOGGER.info("mapNodes: " + mapNodes.toString());
        LOGGER.info("commonNodesTest: " + commonNodesString.toString());
        for (String keyWord : mapNodes.keySet()) {
            createFile(keyWord, mapNodes.get(keyWord), config.getOutputPath(), null, null);
        }
    }

    public void prepareMergeKeyFile(final String key, final File file) throws Exception {
        LOGGER.info("Key: " + key + " && File: " + file.getAbsolutePath());
        MergeEntity entity = new MergeEntity(key);
        NodeList sourceDocChildNodes = getDocumentBuilder(file);
        for (int i = 0; i < sourceDocChildNodes.getLength(); i++) {
            NodeList childNodes2 = sourceDocChildNodes.item(i).getChildNodes();
            for (int j = 0; j < childNodes2.getLength(); j++) {
                NamedNodeMap attributes = childNodes2.item(j).getAttributes();
                if (attributes != null && attributes.getNamedItem("name") != null) {
                    Node node = childNodes2.item(j).getAttributes().getNamedItem("name");
                    Node clone = childNodes2.item(j).cloneNode(true);
                    clone.setNodeValue(node.getNodeValue());
                    MergeRef refOtherKey = isMatchOtherKeyOrBase(node, key, config.getKeys(), config.getBase());
                    /*if (isMatchingNode(node, key)) {
                        //entity.getNodeMatch().add(clone);
                        //entity.getNodeMatchString().add(node.getNodeValue());
                    } else if (refOtherKey.isRef()) {
                        entity.getKeysMatch().add(refOtherKey.getName());
                    } else {
                        commonNodes.put(node.getNodeValue(), clone);
                    }*/
                }
            }
        }
        mapNodes.put(key, entity);
    }


}