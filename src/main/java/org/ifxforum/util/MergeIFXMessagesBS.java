//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.ifxforum.util;

import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MergeIFXMessagesBS {
    static final String outputEncoding = "UTF-8";
    private Set<String> initialTagSet = new HashSet();
    private Set<String> processedTagSet = new HashSet();
    private Set<String> excludedTagSet = new HashSet();
    private String DEFAULT_BASE_FILE = "IFX_Base_2X.xsd";

    public MergeIFXMessagesBS() {
    }

    public void processFiles(String[] args) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        this.loadTagsFromAllFiles(args);
        Document basedoc = this.createSkeletonFile(args[0]);
        String[] var6 = args;
        int var5 = args.length;

        for (int var4 = 0; var4 < var5; ++var4) {
            String fileName = var6[var4];
            this.mergeElements(fileName, this.DEFAULT_BASE_FILE, basedoc);
        }

        this.writeNode(this.DEFAULT_BASE_FILE, basedoc);
    }

    private void mergeElements(String fileName, String baseFileName, Document basedoc) throws TransformerException, ParserConfigurationException, SAXException, IOException {
        File schemaFile = new File(fileName);
        Document messageFile = this.createSkeletonFile(fileName);
        Element createElementNS = messageFile.createElement("xsd:include");
        createElementNS.setAttribute("schemaLocation", baseFileName);
        Node importNode = messageFile.importNode(createElementNS, true);
        messageFile.getDocumentElement().appendChild(importNode);
        Set messageTypes = this.parseMessageTypes(schemaFile);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        OutputStreamWriter errorWriter = new OutputStreamWriter(System.err, "UTF-8");
        db.setErrorHandler(new MergeIFXMessagesBS.MergeIFXMessagesErrorHandler(new PrintWriter(errorWriter, true)));
        Document sourceDoc = db.parse(schemaFile);
        File f = new File(fileName);
        NodeList sourceDocChildNodes = sourceDoc.getChildNodes();

        for (int i = 0; i < sourceDocChildNodes.getLength(); ++i) {
            NodeList childNodes2 = sourceDocChildNodes.item(i).getChildNodes();

            for (int j = 0; j < childNodes2.getLength(); ++j) {
                NamedNodeMap attributes = childNodes2.item(j).getAttributes();
                if (attributes != null && attributes.getNamedItem("name") != null) {
                    String nodeName = childNodes2.item(j).getAttributes().getNamedItem("name").getNodeValue();
                    if (!this.processedTagSet.contains(nodeName)) {
                        Node cloneNode;
                        Node imported;
                        if (messageTypes.contains(nodeName)) {
                            cloneNode = childNodes2.item(j).cloneNode(true);
                            imported = messageFile.importNode(cloneNode, true);
                            messageFile.getDocumentElement().appendChild(imported);
                            System.out.println("Added " + nodeName + " to " + f.getName());
                        } else {
                            cloneNode = childNodes2.item(j).cloneNode(true);
                            imported = basedoc.importNode(cloneNode, true);
                            basedoc.getDocumentElement().appendChild(imported);
                            this.processedTagSet.add(nodeName);
                            System.out.println("Added " + nodeName + " to " + baseFileName);
                        }
                    }
                }
            }
        }

        this.writeNode(fileName, messageFile);
    }

    private Document createSkeletonFile(String baseFile) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document template = db.parse(new File(baseFile));
        Document parent = db.newDocument();
        parent.setDocumentURI(template.getDocumentURI());
        parent.setXmlVersion(template.getXmlVersion());
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
        System.out.println("Writing file " + outputFile);
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

        System.out.println("Wrote " + outputFile);
    }

    private void loadTagsFromAllFiles(String[] args) throws SAXException, IOException, ParserConfigurationException {
        for (int fIndex = 0; fIndex < args.length; ++fIndex) {
            System.out.println("Reading " + args[fIndex]);
            File schemaFile = new File(args[fIndex]);
            this.loadTags(schemaFile);
            Set tags = this.parseMessageTypes(schemaFile);
            Iterator var6 = tags.iterator();

            while (var6.hasNext()) {
                String type = (String) var6.next();
                this.excludedTagSet.add(type);
            }
        }

    }

    private Set<String> parseMessageTypes(File base) {
        HashSet types = new HashSet();
        String[] split = base.getName().split("\\.");
        String baseFile = split[0];
        types.add(baseFile);
        types.add(baseFile + "_Type");
        return types;
    }

    private void loadTags(File tagFile) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        OutputStreamWriter errorWriter = new OutputStreamWriter(System.err, "UTF-8");
        db.setErrorHandler(new MergeIFXMessagesBS.MergeIFXMessagesErrorHandler(new PrintWriter(errorWriter, true)));
        Document doc = db.parse(tagFile);
        NodeList childNodes = doc.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); ++i) {
            NodeList childNodes2 = childNodes.item(i).getChildNodes();

            for (int j = 0; j < childNodes2.getLength(); ++j) {
                NamedNodeMap attributes = childNodes2.item(j).getAttributes();
                if (attributes != null) {
                    if (attributes.getNamedItem("name") != null) {
                        this.initialTagSet.add(childNodes2.item(j).getAttributes().getNamedItem("name").getNodeValue());
                    } else {
                        System.out.println(childNodes2.item(j).getNodeName() + " has no name");
                    }
                }
            }
        }

    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java -jar ifx-merge-messages.jar <xsd file1> <xsd file2> ...");
            System.out.println("Note: Program will overwrite source xsd files if they are in the current working directory");
            System.exit(0);
        }

        MergeIFXMessagesBS merger = new MergeIFXMessagesBS();
        merger.processFiles(args);
    }

    private static class MergeIFXMessagesErrorHandler implements ErrorHandler {
        private PrintWriter out;

        MergeIFXMessagesErrorHandler(PrintWriter out) {
            this.out = out;
        }

        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }

            String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
            return info;
        }

        public void warning(SAXParseException spe) throws SAXException {
            this.out.println("Warning: " + this.getParseExceptionInfo(spe));
        }

        public void error(SAXParseException spe) throws SAXException {
            String message = "Error: " + this.getParseExceptionInfo(spe);
            throw new SAXException(message);
        }

        public void fatalError(SAXParseException spe) throws SAXException {
            String message = "Fatal Error: " + this.getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }
}
