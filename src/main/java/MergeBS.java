import org.ifxforum.util.MergeIFXMessages;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

public class MergeBS extends MergeIFXMessages {

    public static void main(String[] args) throws Exception {
        File file = new File("src/main/resources/schemas");
        File[] schemas = file.listFiles();
        System.out.println(schemas);
        MergeBS merger = new MergeBS();
        merger.processFiles(merger.processFiles(schemas));
    }

    public String[] processFiles(File[] files) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        String[] args = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            args[i] = files[i].getAbsolutePath();
            System.out.println(args[i]);
        }
        return args;
    }

}
