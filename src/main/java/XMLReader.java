import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import  Exception.*;


public class XMLReader {

    private final Logger Log = LoggerFactory.getLogger(XMLReader.class);

    /**
     *  Method to read Entities of an existing XML File
     * @param xmlFileLocation   Location of the XML-File
     *@return entities  Hashmap containing all entities of the XML File
     @exception Invalid_input_Exception  throws if an error occurs during the reading process
     */
    public HashMap<String, Entity_DTO> readConfig(String xmlFileLocation) throws Invalid_input_Exception {
        try {
            File inputFile = new File(xmlFileLocation);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            Log.info("XML File found");
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("entity");
            Log.info(nList.getLength()+" Entities found");
            HashMap<String, Entity_DTO> entities=new HashMap<String,Entity_DTO>();

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Log.info("Entity nr 1");

                Node nNode = nList.item(temp);
                NodeList child= nNode.getChildNodes();
                Log.info(""+child.getLength());
                Entity_DTO entity=new Entity_DTO();
                String id=null;
                for (int temp2 = 0; temp2 < child.getLength(); temp2++) {
                    Node childnode = child.item(temp2);
                    if (childnode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) childnode;
                        Log.info("Feature tag="+eElement.getAttribute("tag")+" ;value= "+eElement.getAttribute("value"));

                        entity.addFeature(eElement.getAttribute("tag"),eElement.getAttribute("value"));

                        if(eElement.getAttribute("tag").contains("id")){

                            id=eElement.getAttribute("value");

                        }
                    }
                }
                entities.put(id,entity);

            }
            return  entities;
        }
        catch (ParserConfigurationException e1){
            Log.info("XML reading Error");
            throw new Invalid_input_Exception("ParserConfigurationException");

        }
        catch (SAXException e2){
            Log.info("XML reading Error");
            throw new Invalid_input_Exception("SaxException");

        }
        catch (IOException e3){
            Log.info("XML reading Error");
            throw new Invalid_input_Exception("IOException");

        }

    }

}

