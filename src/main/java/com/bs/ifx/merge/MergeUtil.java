package com.bs.ifx.merge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class MergeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeUtil.class);

    @Value("${xml.version}")
    private String xmlVersion;

    public Document createSkeletonFile() throws ParserConfigurationException, SAXException, IOException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        //Document template = db.parse(new File(baseFile));
        Document parent = db.newDocument();
        //parent.setDocumentURI(template.getDocumentURI());
        parent.setXmlVersion(xmlVersion);
        Element schema = parent.createElement("xsd:schema");
        schema.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        schema.setAttribute("xmlns", "http://www.ifxforum.org/IFX_2X");
        schema.setAttribute("attributeFormDefault", "unqualified");
        schema.setAttribute("elementFormDefault", "qualified");
        schema.setAttribute("targetNamespace", "http://www.ifxforum.org/IFX_2X");
        parent.appendChild(schema);
        return parent;
    }

    private void writeNode(String fileName, Node node) throws TransformerException, FileNotFoundException {
        String outputFile = (new File(fileName)).getName();
        LOGGER.info("Writing file " + outputFile);
        FileOutputStream fos = null;

        try {
            TransformerFactory e = TransformerFactory.newInstance();
            Transformer transformer = e.newTransformer();
            DOMSource source = new DOMSource(node);
            fos = new FileOutputStream(outputFile, false);
            StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);
            fos.flush();
        } catch (IOException var17) {
            var17.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException var16) {
                    var16.printStackTrace();
                }
            }

        }
        LOGGER.info("Wrote " + outputFile);
    }

}
