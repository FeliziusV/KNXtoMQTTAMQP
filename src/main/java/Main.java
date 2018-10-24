/**
 * Main Class
 *
 * @author Felix Walcher
 * @version 0.1
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;


public class Main {
    private final Logger Log = LoggerFactory.getLogger(Main.class);
    public static boolean active = true;


    public static void main(String[] args) {

/*
        XMLReader xmlReader = new XMLReader();
        try {

            HashMap<String, Entity_DTO> tag_model = xmlReader.readConfig("knx_input_model.xml");

            PropertiesManager prop = new PropertiesManager("KNXtoMQTTAMQP.config");
            DatapointManager datapointManager = new DatapointManager(tag_model, prop);
            datapointManager.setUpConnection();

            while (active) {
                datapointManager.readDatapoints();
            }
            datapointManager.disconnect();
        } catch (Invalid_input_Exception e1) {

            return;

        } catch (IoT_Connection_Exception e2) {
            return;
        }

*/
try {
    MQTT_Communication c = new MQTT_Communication("tcp://localhost:1883", 0);
    c.publishMessage("x","x");
}
catch (IoT_Connection_Exception e){

}
    }

}

