package com.bs.ifx.merge.util;

import com.bs.ifx.merge.conf.MergeConfig;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class MergeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeUtil.class);

    public Document createSkeletonFile() throws ParserConfigurationException, SAXException, IOException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        //Document template = db.parse(new File(baseFile));
        Document parent = db.newDocument();
        //parent.setDocumentURI(template.getDocumentURI());
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

}
