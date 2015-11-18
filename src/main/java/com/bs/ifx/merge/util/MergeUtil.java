package com.bs.ifx.merge.util;

import com.bs.ifx.merge.conf.MergeConfig;
import com.bs.ifx.merge.entities.MergeEntity;
import com.bs.ifx.merge.entities.MergeErrorHandler;
import com.bs.ifx.merge.entities.MergeFileFilter;
import com.bs.ifx.merge.entities.MergeRef;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class MergeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeUtil.class);

    public Document createSkeletonFile(final String key, final Set<String> ns) throws Exception {
        if (MergeConfig.NS) {
            return createSkeletonFileNS(key, ns);
        } else {
            return createSkeletonFileNS("", new HashSet<>());
        }
    }

    private Document createSkeletonFileNS(final String key, final Set<String> ns)
            throws ParserConfigurationException, SAXException, IOException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document parent = db.newDocument();
        parent.setXmlVersion(MergeConfig.VERSION);
        Element schema = parent.createElement("xsd:schema");
        schema.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        schema.setAttribute("xmlns", MergeConfig.BASE_NS + key);

        if (MergeConfig.NS && !key.equals(MergeConfig.DATATYPE_XSD)) {
            for (String nspace : ns) {
                schema.setAttribute("xmlns:" + getPrefixByKey(nspace), MergeConfig.BASE_NS + nspace);
            }
            if (!key.equals(MergeConfig.COMMON_XSD)) {
                schema.setAttribute("xmlns:com", MergeConfig.BASE_NS + MergeConfig.COMMON_XSD);
            }
        }

        schema.setAttribute("attributeFormDefault", "unqualified");
        schema.setAttribute("elementFormDefault", "qualified");
        schema.setAttribute("targetNamespace", MergeConfig.BASE_NS + key);
        parent.appendChild(schema);
        return parent;
    }

    public OutputFormat getPrettyPrintFormat() {
        OutputFormat format = new OutputFormat();
        format.setLineWidth(100);
        format.setIndenting(true);
        format.setIndent(4);
        format.setEncoding("UTF-8");
        return format;
    }

    public void document2File(final Node doc, final File file) throws Exception {
        XMLSerializer serializer = new XMLSerializer(new FileOutputStream(file), getPrettyPrintFormat());
        loggerDocumentString(doc);
        serializer.serialize(doc);
    }

    public void loggerDocumentString(final Node doc) throws Exception {
        StringWriter stringOut = new StringWriter();
        XMLSerializer serial = new XMLSerializer(stringOut, getPrettyPrintFormat());
        serial.serialize(doc);
        LOGGER.debug(stringOut.toString());
    }

    //TODO: Revisar Format & Ident
    public void writeNode(final String parent, final String fileName, final Node node) throws Exception {
        File outputFile = new File(parent, fileName);
        document2File(node, outputFile);

        /*TransformerFactory t = TransformerFactory.newInstance();
        Transformer transformer = t.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setAttribute("indent-number", 2);
        t.setAttribute("width", "50");

        FileOutputStream fos = null;
        try {

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(node);

            fos = new FileOutputStream(new File(parent, fileName), false);
            StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);
            fos.flush();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

        }
        LOGGER.info("Wrote " + outputFile.getName());*/
    }

    private String getPrefixByKey(final String key) {
        return key.substring(0, 3).toLowerCase();
    }

    public void prepareOutput(final String outputPath) throws IOException {
        File destination = new File(outputPath);
        if (destination.exists()) {
            LOGGER.info("Deleting....");
            FileUtils.deleteDirectory(destination);
        }
        if (!destination.mkdirs()) {
            throw new IOException("OutputPath is not create!!");
        }
    }

    public void prepareInput(final String inputPath) throws Exception {
        File inputDirectory = new File(inputPath);
        if (!inputDirectory.isDirectory()) {
            throw new Exception("InputPath is not valid!!");
        } else {
            if (inputDirectory.listFiles() == null || inputDirectory.list(new MergeFileFilter()).length <= 0) {
                throw new Exception("InputPath not contains XSD files");
            }
        }
    }

    public NodeList getDocumentBuilder(final File file) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        OutputStreamWriter errorWriter = new OutputStreamWriter(System.err, MergeConfig.ENCODING);
        db.setErrorHandler(new MergeErrorHandler(new PrintWriter(errorWriter, true)));
        return db.parse(file).getChildNodes();
    }

    public MergeRef isMatchNodeWithKeys(final String subNode, final String currentKey, final Set<String> keys) {
        for (String keyWord : keys) {
            if (keyWord.equals(currentKey)) {
                continue;
            }
            if (isMatchingNode(subNode, keyWord)) {
                return new MergeRef(keyWord);
            }
        }
        return new MergeRef();
    }

    public boolean isMatchingNodeBase(final Node node, final Set<String> base) {
        for (String keyWord : base) {
            if (node.getNodeValue().equalsIgnoreCase(keyWord)) {
                return true;
            }
        }
        return false;
    }

    public String getKeyMatchingNode(final Node node, final Set<String> keys) {
        for (String keyWord : keys) {
            if (node.getNodeValue().startsWith(keyWord)) {
                return keyWord;
            }
        }
        return null;
    }

    public Set<String> iterateElements(final NodeList list) {
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
                    depthKeys.addAll(iterateElements(list2));
                }
            }
        }
        return depthKeys;
    }

    public boolean isMatchingNode(final String value, final String key) {
        if (value.startsWith(key)) {
            return true;
        }
        return false;
    }

    public void createXSDInclude(final Document messageFile, final MergeEntity entity) {
        for (String key : entity.getKeysMatch()) {
            createXSDInclude(messageFile, key);
        }
    }

    public void createXSDInclude(final Document messageFile, final String name) {
        if (MergeConfig.NS) {
            Element createElementNS = messageFile.createElement("xsd:import");
            createElementNS.setAttribute("schemaLocation", name + MergeConfig.EXT_XSD);
            createElementNS.setAttribute("namespace", MergeConfig.BASE_NS + name);
            Node importNode = messageFile.importNode(createElementNS, true);
            messageFile.getDocumentElement().appendChild(importNode);
        } else {
            Element createElementNS = messageFile.createElement("xsd:include");
            createElementNS.setAttribute("schemaLocation", name + ".xsd");
            Node importNode = messageFile.importNode(createElementNS, true);
            messageFile.getDocumentElement().appendChild(importNode);
        }
    }

    public void createFile(final String currentKey, final MergeEntity entity, final String outPutPath)
            throws Exception {
        if (!entity.hasEmptyNodes() || currentKey.equalsIgnoreCase(MergeConfig.DATATYPE_XSD)) {
            Document messageFile = createSkeletonFile(currentKey, entity.getKeysMatch());
            createXSDInclude(messageFile, entity);
            for (Node imported : entity.getNodeMatch().values()) {
                Node imported2 = messageFile.importNode(imported, true);
                messageFile.getDocumentElement().appendChild(imported2);
            }
            writeNode(outPutPath, currentKey + MergeConfig.EXT_XSD, messageFile);
        }
    }

    public String getKeysByValue(final Map<String, Set<String>> map, final String v) {
        String valueType = v.replaceAll("_Type", "");
        for (Map.Entry<String, Set<String>> mapEntry : map.entrySet()) {
            if (map.get(mapEntry.getKey()).contains(v) || map.get(mapEntry.getKey()).contains(valueType)) {
                return mapEntry.getKey();
            }
        }
        return "";
    }

}


