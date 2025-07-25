package toolbox.XMLParser;

import java.util.HashMap;

public class XMLNode {
    private HashMap<String,XMLNode> child_nodes=new HashMap<>();
    private HashMap<String,String> attributes=new HashMap<>();
    String name;
    String value;//what is in between the <> </>

}
