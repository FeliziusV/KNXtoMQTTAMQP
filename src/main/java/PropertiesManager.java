/**
 * PropertiesManager
 *opens a Config File and allows access to its config values
 * @author
 * @version 0.1
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import  Exception.*;


public class PropertiesManager {

  private  Properties prop;

    private final Logger Log = LoggerFactory.getLogger(Main.class);

    /**
     * Constructor to create a new Properties Manager
     * @param name   Location of the .config File
     * @exception Invalid_input_Exception  throws if an error occurs during the reading process
     */
    public PropertiesManager(String name) throws Invalid_input_Exception {

       Log.info("reading Properties");
        prop = new java.util.Properties();
       InputStream is = null;
       try {
           is = new FileInputStream(name);
       } catch (FileNotFoundException ex) {
           Log.info("Properties File not found");
           throw new Invalid_input_Exception("FileNotFoundException");
       }
       try {
           prop.load(is);
       } catch (IOException ex) {
           Log.info("Properties reading faild");

           throw new Invalid_input_Exception("FileNotFoundException");
       }
   }
    /**
     *  Methode to read a Property
     * @param name   name of the Property
     * @return value    the value of the requested Property
     @exception Invalid_input_Exception  throws if an error occurs during  reading the property
     */
   public String readProperties(String name) throws Invalid_input_Exception {
        String value=prop.getProperty(name);
        if(value==null){
            Log.info("invalid Properties-File:"+name);
            throw new Invalid_input_Exception("invalid Properties-File");
        }
       return value;
   }
}
