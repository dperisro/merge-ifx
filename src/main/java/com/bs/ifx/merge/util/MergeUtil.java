package com.bs.ifx.merge.util;

import com.bs.ifx.merge.conf.MergeConfig;
import com.bs.ifx.merge.entities.MergeEntity;
import com.bs.ifx.merge.entities.MergeErrorHandler;
import com.bs.ifx.merge.entities.MergeFileFilter;
import com.bs.ifx.merge.entities.MergeRef;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

@Component
public class MergeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeUtil.class);

    public Document createSkeletonFile() throws ParserConfigurationException, SAXException, IOException, TransformerException {
        return createSkeletonFile(null);
    }

    public Document createSkeletonFile(final File fileBase) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document parent = db.newDocument();
        if (fileBase != null) {
            Document template = db.parse(fileBase);
            parent.setDocumentURI(template.getDocumentURI());
        }
        parent.setXmlVersion(MergeConfig.VERSION);
        Element schema = parent.createElement("xsd:schema");
        schema.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        schema.setAttribute("xmlns", "http://www.ifxforum.org/IFX_2X");
        schema.setAttribute("attributeFormDefault", "unqualified");
        schema.setAttribute("elementFormDefault", "qualified");
        schema.setAttribute("targetNamespace", "http://www.ifxforum.org/IFX_2X");
        parent.appendChild(schema);
        return parent;
    }

    //TODO: Revisar Format & Ident
    public void writeNode(final String parent, final String fileName, final Node node) throws TransformerException, FileNotFoundException {
        File outputFile = new File(parent, fileName);
        LOGGER.info("Writing file " + outputFile.getName());
        FileOutputStream fos = null;

        try {
            TransformerFactory e = TransformerFactory.newInstance();
            Transformer transformer = e.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            e.setAttribute("indent-number", 2);
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
        LOGGER.info("Wrote " + outputFile.getName());
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

    public DocumentBuilder getDocumentBuilder() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        OutputStreamWriter errorWriter = new OutputStreamWriter(System.err, "UTF-8");
        db.setErrorHandler(new MergeErrorHandler(new PrintWriter(errorWriter, true)));
        return db;
    }

    public MergeRef isMatchingNodeOtherKeys(final Node node, final String currentKey, final List<String> keys) {
        for (String keyWord : keys) {
            if (keyWord.equals(currentKey)) {
                continue;
            }
            if (isMatchingNode(node, keyWord)) {
                return new MergeRef(keyWord);
            }
        }
        return new MergeRef();
    }

    public boolean isMatchingNode(final Node node, final String key) {
        if (node.getNodeValue().startsWith(key)) {
            return true;
        }
        return false;
    }

    public void createXSDInclude(final Document messageFile, final MergeEntity entity) {
        createXSDInclude(messageFile, MergeConfig.COMMON_XSD);
        for (String key : entity.getKeysMatching()) {
            createXSDInclude(messageFile, key);
        }
    }

    public void createXSDInclude(final Document messageFile, final String name) {
        Element createElementNS = messageFile.createElement("xsd:include");
        createElementNS.setAttribute("schemaLocation", name + ".xsd");
        Node importNode = messageFile.importNode(createElementNS, true);
        messageFile.getDocumentElement().appendChild(importNode);
    }

}
