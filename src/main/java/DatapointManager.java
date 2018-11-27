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
import Exception.*;
import org.w3c.dom.NamedNodeMap;


public class DatapointManager {

    private HashMap<String, Datapoint> DatapointMap=new HashMap<String, Datapoint>();
    private PropertiesManager prop;
    private MQTT_Communication MQTT_con;
    private AMQP_Communication AMQP_con;
    private KNX_Communication KNX_con;
    private Thread mqtt_listener;
    private Thread amqp_listener;


    private boolean MQTT=false;
    private boolean AMQP=false;

    private final Logger Log = LoggerFactory.getLogger(Main.class);


    /**
     * Constructor for a new Datapoint Manager. Creates new Datapoints for each Entity that contains the datapoint tags
     * @param tag_model   Hashmap containing all KNX entities
     * @param prop  Configuration File
     * @exception Invalid_input_Exception  throws if an error occurs during the reading process
     */

    public DatapointManager(HashMap<String, NamedNodeMap> tag_model, PropertiesManager prop ) throws Invalid_input_Exception {

        this.prop=prop;
        for (Map.Entry<String, NamedNodeMap> pair : tag_model.entrySet()) {
            System.out.println(pair.getKey());
            NamedNodeMap map=pair.getValue();
            System.out.println(map.getLength());

            String id=null;
            String address=null;
            String datapointRef=null;
            String topic=null;
            String datatype=null;
            String readable=null;


            for (int j = 0; j < map.getLength(); j++) {
                System.out.println(map.item(j).getNodeName()+":"+map.item(j).getNodeValue());

                if(map.item(j).getNodeName().contains("id")){
                    id=map.item(j).getNodeValue().replaceAll("^\"|\"$", "");
                }
                if(map.item(j).getNodeName().contains("groupAddress")){
                    address=map.item(j).getNodeValue().replaceAll("^\"|\"$", "");

                }
                if(map.item(j).getNodeName().contains("datapointRef")){
                    datapointRef=map.item(j).getNodeValue().replaceAll("^\"|\"$", "");

                }
                if(map.item(j).getNodeName().contains("readable")){
                    readable=map.item(j).getNodeValue().replaceAll("^\"|\"$", "");

                }






            }
            System.out.println(id);
            System.out.println(address);
            System.out.println(datapointRef);
            if(datapointRef!=null&&readable!=null) {
                if(datapointRef.length()<=6&&readable.contains("true")){


                Log.info("x"+datapointRef+"x");
                NamedNodeMap map2 = tag_model.get(datapointRef);
                for (int jj = 0; jj < map2.getLength(); jj++) {
                    if (map2.item(jj).getNodeName().contains("name")) {
                        topic = map2.item(jj).getNodeValue().replaceAll("^\"|\"$", "");
                    }
                    if (map2.item(jj).getNodeName().contains("description")) {
                        String d = map2.item(jj).getNodeValue().replaceAll("^\"|\"$", "");
                        if (d.contains("Ein / Aus")) {
                            datatype = "boolean";

                        } else {
                            datatype = "double";

                        }
                    }
                }
                }
                System.out.println(topic);
                System.out.println(datatype);
                DatapointMap.put(id,new Datapoint(id,topic,address,datatype));


            }


        }

        if(DatapointMap.isEmpty()){
            Log.error("no valid Datapoint found");
            throw new Invalid_input_Exception("no valid Datapoint found");
        }

        if (prop.readProperties("mode").equals("MQTT")) {
            MQTT=true;

        }
        if (prop.readProperties("mode").equals("AMQP")) {
            AMQP=true;

        }




    }
    /**
     * Method to setUp the chosen IoT Communication
     *  @exception Invalid_input_Exception  throws if an error occurs during the reading process
     */
    public void setUpConnection() throws Invalid_input_Exception, IoT_Connection_Exception,KNX_Connection_Exception {

        if(MQTT){
            setUpMQTT();

        }

        else {
            setUpAMQP();
        }


    }
    private void setUpMQTT() throws Invalid_input_Exception, IoT_Connection_Exception,KNX_Connection_Exception {
        Log.info("MQTT Connection setUp");

        final String MQTT_broker_url = prop.readProperties("MQTT_broker_url").trim();;
        String MQTT_qos = null;
        String MQTT_topic = null;
        int qos = 0;




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
        String Local_IP=prop.readProperties("Local_Ip");
        KNX_con= new KNX_Communication(KNX_host,Local_IP);
        mqtt_listener = new Thread(() -> {
            try {
                new MQTT_Listener().subscribe("MQTT_input", MQTT_broker_url, KNX_con);
            }
            catch (IoT_Connection_Exception e){

                Log.error(e.getMessage());
            }


        });
        mqtt_listener.run();


    }
    private void setUpAMQP() throws Invalid_input_Exception, IoT_Connection_Exception,KNX_Connection_Exception {


        final String userName = prop.readProperties("AMQP_userName").trim();
        final String password = prop.readProperties("AMQP_password").trim();
        final String virtualHost = prop.readProperties("AMQP_virtualHost").trim();
        final String hostName = prop.readProperties("AMQP_hostName").trim();
        final  String portNumber = prop.readProperties("AMQP_port").trim();
        String Local_IP=prop.readProperties("Local_Ip");


        ArrayList<String> topiclist=new ArrayList<String>();
        for (Map.Entry<String, Datapoint> pair : DatapointMap.entrySet()) {
            Datapoint datapoint = pair.getValue();

            topiclist.add(datapoint.getTopic());

        }


        AMQP_con = new AMQP_Communication(userName, password, virtualHost, hostName, Integer.parseInt(portNumber), topiclist);

        String KNX_host=prop.readProperties("KNX_host");
        KNX_con= new KNX_Communication(KNX_host,Local_IP);
        amqp_listener = new Thread(() -> {
            AMQP_Listener al = new AMQP_Listener(userName, password, virtualHost, hostName, Integer.parseInt(portNumber), "AMQP_input", KNX_con);




        });
        amqp_listener.run();

    }

    /**
     * Method to disconnect the Connection to an IoT Broker
     *
     */
    public void disconnect() throws Invalid_input_Exception, IoT_Connection_Exception {
        Log.info("hier");

        if(MQTT){
            MQTT_con.disconnect();
            KNX_con.disconnect();
        }
        else{
            Log.info("now");
            AMQP_con.disconnect();
            KNX_con.disconnect();

        }



    }

    private void publishMQTT(String topic,String message) throws IoT_Connection_Exception {
        MQTT_con.publishMessage(topic,message);
    }


    private void publishAMQP(String topic,String message) throws IoT_Connection_Exception {
        AMQP_con.publishMessage(message,"ex"+topic,"key");
    }

    public void readDatapoints() throws IoT_Connection_Exception,KNX_Connection_Exception {
        for (Map.Entry<String, Datapoint> pair : DatapointMap.entrySet()) {
            Datapoint datapoint=pair.getValue();
            String value="";
            if(datapoint.getDataType().equals("double")) {
                value = datapoint.getName()+" device=KNXtoMQTTAMQP value=" + KNX_con.readDouble(datapoint.getGroupAddress());
            }
            else if(datapoint.getDataType().equals("boolean")) {
                value = datapoint.getName()+"device=KNXtoMQTTAMQP value=" + KNX_con.readBoolean(datapoint.getGroupAddress());
            }
            else if(datapoint.getDataType().equals("String")){
                value = datapoint.getName()+"device=KNXtoMQTTAMQP value=" + KNX_con.readString(datapoint.getGroupAddress());
            }
            if(MQTT){
                publishMQTT(datapoint.getTopic(),value);
            }
            else{
                publishAMQP(datapoint.getTopic(),value);
            }
        }
    }


}