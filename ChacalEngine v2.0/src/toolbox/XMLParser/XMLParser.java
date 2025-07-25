package toolbox.XMLParser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.io.*;

public class XMLParser {
    public static Document read_XML_file(String path){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(path);
            document.getDocumentElement().normalize();
            return document;

        } catch (Exception e) {
            e.printStackTrace();
        }
        //String text="";
       /* try {
            BufferedReader reader=new BufferedReader(new FileReader(new File(path)));
            text =reader.readLine();
            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //preprocess the text i.e. remove all of the empty lines the space before text
        String[] lines=text.split("\n");*/

        return null;
    }
}
