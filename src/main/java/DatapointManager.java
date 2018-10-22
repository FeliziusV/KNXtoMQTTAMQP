/**
 * DatapointManager Class
 *
 * @author Felix Walcher
 * @version 0.1
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatapointManager {

    private HashMap<String, Datapoint> DatapointMap=new HashMap<String, Datapoint>();
    private PropertiesManager prop;
    private MQTT_Communication MQTT_con;
    private AMQP_Communication AMQP_con;
    private KNX_Communication KNX_con;

    private boolean MQTT=false;
    private boolean AMQP=false;

    private final Logger Log = LoggerFactory.getLogger(Main.class);


    /**
     * Constructor for a new Datapoint Manager. Creates new Datapoints for each Entity that contains the Datapoint Tag
     * @param tag_model   Hashmap containing all KNX entities
     * @param prop  Configuration File
     * @exception Invalid_input_Exception  throws if an error occurs during the reading process
     */

    public DatapointManager(HashMap<String, Entity_DTO> tag_model,PropertiesManager prop ) throws Invalid_input_Exception {

        this.prop=prop;
        for (Map.Entry<String, Entity_DTO> pair : tag_model.entrySet()) {
            Entity_DTO entity = pair.getValue();
            if (entity.containsFeature("datapoint")) {
                Log.info("Datapoint found");
                Datapoint datapoint = new Datapoint(entity.getFeature("id"),entity.getFeature("groupAddress"),entity.getFeature("valueTyoe"));
                Log.info("new Datapoint created");
                String topic = entity.getFeature("id");
                Log.info(topic);
                boolean rec = true;
                boolean change=false;
                String search=entity.getFeature("id");
                while (rec) {
                    change=false;
                    for (Map.Entry<String, Entity_DTO> pair2 : tag_model.entrySet()) {
                        Log.info("search "+search);
                        Entity_DTO entity2 = pair2.getValue();
                        if (entity2.containsFeature("datapointRef")) {
                            if (entity2.getFeature("datapointRef").contains(search)) {
                                topic = entity2.getFeature("id") + "/" + topic;
                                Log.info(topic);
                                search = entity2.getFeature("id");
                                Log.info(search);
                                change=true;

                            }
                        }
                        if (entity2.containsFeature("buildingPart")) {
                            Log.info("ja");
                            if (entity2.getFeature("buildingPart").contains(search)) {
                                Log.info("ja2");
                                topic = entity2.getFeature("id") + "/" + topic;
                                Log.info(topic);
                                search = entity2.getFeature("id");
                                change=true;

                            }
                        }



                    }
                    if(change==false){
                        rec=false;
                    }

                }
                datapoint.setTopic(topic);
                DatapointMap.put(datapoint.getName(),datapoint);
            }


        }
        try {
            if (prop.readProperties("mode").contains("MQTT")) {
                MQTT=true;

            }
            if (prop.readProperties("mode").contains("AMQP")) {
                AMQP=true;

            }
        }
        catch (Invalid_input_Exception e) {
            Log.info("Invalid Properties-File");
            throw new Invalid_input_Exception("");
        }


    }
    /**
     * Method to setUp the chosen IoT Communication
     *  @exception Invalid_input_Exception  throws if an error occurs during the reading process
     */
    public void setUpConnection() throws Invalid_input_Exception, IoT_Connection_Exception {

        if(MQTT){
                setUpMQTT();

            }

      /*  if(AMQP){
            setUpAMQP();
        }
        */

    }
    private void setUpMQTT() throws Invalid_input_Exception, IoT_Connection_Exception {
        Log.info("MQTT Connection setUp");

        String MQTT_broker_url = null;
        String MQTT_qos = null;
        String MQTT_topic = null;
        int qos = 0;



        MQTT_broker_url = prop.readProperties("MQTT_broker_url").trim();
        MQTT_qos = prop.readProperties("MQTT_qos").trim();
        if (MQTT_qos.contains("0")) {
            qos = 0;
        }
        if (MQTT_qos.contains("1")) {
            qos = 1;

        }
        if (MQTT_qos.contains("2")) {
            qos = 2;
        }
        if (!MQTT_qos.contains("0") && !MQTT_qos.contains("1") && !MQTT_qos.contains("2")) {
            Log.info("Invalid QOS");
            return;
        }


        MQTT_con = new MQTT_Communication(MQTT_broker_url, qos);

        String KNX_host=prop.readProperties("KNX_host");
        KNX_con= new KNX_Communication(KNX_host,"169.254.232.243");


    }
    private void setUpAMQP() throws Invalid_input_Exception, IoT_Connection_Exception {


            String userNAme = prop.readProperties("AMQP_userName").trim();
            String password = prop.readProperties("AMQP_password").trim();
            String virtualHost = prop.readProperties("AMQP_virtualHost").trim();
            String hostName = prop.readProperties("AMQP_hostName").trim();
            String portNumber = prop.readProperties("AMQP_port").trim();


            ArrayList<String> topiclist=new ArrayList<String>();
            for (Map.Entry<String, Datapoint> pair : DatapointMap.entrySet()) {
                Datapoint datapoint = pair.getValue();
                topiclist.add(datapoint.getTopic());

            }


            AMQP_con = new AMQP_Communication(userNAme, password, virtualHost, hostName, Integer.parseInt(portNumber), topiclist);

        String KNX_host=prop.readProperties("KNX_host");
        KNX_con= new KNX_Communication(KNX_host,"169.254.232.243");

    }

    /**
     * Method to disconnect the Connection to an IoT Broker
     *
     */
    public void disconnect() throws Invalid_input_Exception, IoT_Connection_Exception {

        if(MQTT){
            MQTT_con.disconnect();
            KNX_con.disconnect();
        }
        if(AMQP){
            AMQP_con.disconnect();
            KNX_con.disconnect();

        }



    }
    /**
     * Method to publish a Message to an IoT Broker
     *
     */
    public void publishMessage(String topic, String message) throws IoT_Connection_Exception {

            if( MQTT){
                publishMQTT( topic,  message);
            }
            if(AMQP){
                publishAMQP( topic,  message);



            }


    }

    private void publishMQTT(String topic,String message) throws IoT_Connection_Exception {
        MQTT_con.publishMessage(topic,message);
    }


    private void publishAMQP(String topic,String message) throws IoT_Connection_Exception {
        AMQP_con.publishMessage(message,"ex"+topic,message+"key");
    }

    public void readDatapoints() throws IoT_Connection_Exception {
         for (Map.Entry<String, Datapoint> pair : DatapointMap.entrySet()) {
             Datapoint datapoint=pair.getValue();

             String value="temperature,device=KNXtoMQTTAMQP value="+KNX_con.readDouble(datapoint.getGroupAdress());
             if(MQTT){
                 publishMQTT(datapoint.getTopic(),value);
             }
             if(AMQP){
                 publishAMQP(KNX_con.readString(datapoint.getName()),value);
             }
         }
    }


}

