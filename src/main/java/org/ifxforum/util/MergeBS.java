package org.ifxforum.util;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class MergeBS extends MergeIFXMessagesBS {

    //private static String CURRENT = Paths.get("").toAbsolutePath().toString();
    private static String CURRENT = "src/main/resources/merge3";

    public static void main(String[] args) throws Exception {
        File file = new File(CURRENT);
        File[] schemas = file.listFiles();
        System.out.println(schemas);
        MergeBS merger = new MergeBS();
        merger.deleteResults();
        merger.processFiles(merger.processFiles(schemas));
    }

    public String[] processFiles(File[] files) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        List<String> listArgs = new LinkedList<String>();
        System.out.println(files.length);
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".xsd")) {
                listArgs.add(files[i].getAbsolutePath());
            }
        }
        return listArgs.toArray(new String[listArgs.size()]);
    }

    public void deleteResults() {
        File folder = new File(Paths.get("").toAbsolutePath().toString());
        for (File f : folder.listFiles()) {
            if (f.getName().endsWith(".xsd")) {
                f.delete();
            }
        }
    }

}
