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
    public HashMap<String, NamedNodeMap> readConfig(String xmlFileLocation) throws Invalid_input_Exception {
        try {
            File inputFile = new File(xmlFileLocation);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            Log.info("XML File found");
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("entity");
            Log.info(nList.getLength()+" Entities found");
            HashMap<String, NamedNodeMap> entities=new HashMap<String,NamedNodeMap>();


            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
               NamedNodeMap nmap=nNode.getAttributes();
              String[] idx=nmap.getNamedItem("id").toString().split("=");
              String id=idx[1];


                id=id.replaceAll("^\"|\"$", "");
                entities.put(id,nmap);

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

