/**
 * DatapointManager Class
 *
 * @author
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
    private HashMap<String,String>datapointType=new HashMap<String,String>();
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
     * Constructor for a new Datapoint Manager. Creates new Datapoints for each Entity
     * @param tag_model   Hashmap containing all KNX entities
     * @param prop  Configuration File
     * @exception Invalid_input_Exception  throws if an error occurs during the reading process
     */

    public DatapointManager(HashMap<String, NamedNodeMap> tag_model, PropertiesManager prop ) throws Invalid_input_Exception {

        this.prop=prop;

        HashMap<String,String>devicelist=new HashMap<String,String>();


        for (Map.Entry<String, NamedNodeMap> pair : tag_model.entrySet()) {
            NamedNodeMap map=pair.getValue();

            String id=null;
            String address=null;
            String datapointRef=null;
            String description=null;
            String datatype=null;
            String readable=null;


            for (int j = 0; j < map.getLength(); j++) {

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



            if(datapointRef!=null&&readable!=null) {
                if(datapointRef.length()<=6){


                    NamedNodeMap map2 = tag_model.get(datapointRef);
                    for (int jj = 0; jj < map2.getLength(); jj++) {
                        if (map2.item(jj).getNodeName().contains("description")) {
                            description = map2.item(jj).getNodeValue().replaceAll("^\"|\"$", "");
                        }



                    }

                    description=description.replace("\\\\\\","");


                    if(description.contains("Ein / Aus")||description.contains("16 Bit")||description.contains("stetig")||description.contains("Heller / Dunkler")||description.contains("Auf / Zu")||description.contains("Auf / Ab")||description.contains("abrufen / beenden")||description.contains("0% ... 100%")||description.contains("1 Bit")) {
                        datapointType.put(address, description);
                    }
                }
                else{
                    String[]a=datapointRef.split("\\|");

                    for(int i=0;i<a.length;i++){

                        NamedNodeMap map2 = tag_model.get(a[i]);
                        for (int jj = 0; jj < map2.getLength(); jj++) {
                            if (map2.item(jj).getNodeName().contains("description")) {
                                description = map2.item(jj).getNodeValue().replaceAll("^\"|\"$", "");
                            }



                        }

                        description=description.replace("\\\\\\","");


                        if(description.contains("Ein / Aus")||description.contains("16 Bit")||description.contains("stetig")||description.contains("Heller / Dunkler")||description.contains("Auf / Zu")||description.contains("Auf / Ab")||description.contains("abrufen / beenden")||description.contains("0% ... 100%")||description.contains("1 Bit")) {
                            if(!datapointType.containsKey(address)) {
                                datapointType.put(address, description);
                            }
                        }

                    }
                }

            }



        }

        for (Map.Entry<String, NamedNodeMap> pair : tag_model.entrySet()) {
            NamedNodeMap map=pair.getValue();
            String datapointRef=null;
            String device=null;
            String name=null;


            for (int j = 0; j < map.getLength(); j++) {

                if(map.item(j).getNodeName().contains("device")){
                    device=map.item(j).getNodeValue().replaceAll("^\"|\"$", "");
                }
                if(map.item(j).getNodeName().contains("datapointRef")){
                    datapointRef=map.item(j).getNodeValue().replaceAll("^\"|\"$", "");

                }
                if(map.item(j).getNodeName().contains("name")){
                    name=map.item(j).getNodeValue().replaceAll("^\"|\"$", "");

                }

            }
            if(device!=null){

                name=name.replaceAll("\\s+","");
                name=name.replace("-","");
                name=name.replace("\\\\\\","");
                name=name.replace("/","");
                if(datapointRef!=null) {
                    String[] devices = datapointRef.split("\\|");

                    for (int i = 0; i < devices.length; i++) {
                        devicelist.put(devices[i], name);
                        Log.info("device "+devices[i]+" found");
                    }
                }
            }

        }


        for (Map.Entry<String, NamedNodeMap> pair : tag_model.entrySet()) {

            NamedNodeMap map=pair.getValue();

            String id=null;
            String address=null;
            String datapointRef=null;
            String topic=null;
            String datatype=null;
            String readable=null;


            for (int j = 0; j < map.getLength(); j++) {

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

            if(datapointRef!=null&&readable!=null) {
                if(datapointRef.length()<=6&&readable.contains("true")){


                    NamedNodeMap map2 = tag_model.get(datapointRef);
                    for (int jj = 0; jj < map2.getLength(); jj++) {
                        if (map2.item(jj).getNodeName().contains("name")) {
                            topic = map2.item(jj).getNodeValue().replaceAll("^\"|\"$", "");
                        }



                    }

                    topic=topic.replaceAll("\\s+","");
                    topic=topic.replace("-","");
                    topic=topic.replace("\\\\\\","");
                    topic=topic.replace("/","");



                    DatapointMap.put(id,new Datapoint(id,devicelist.get(datapointRef)+"/"+topic,address));
                    Log.info("Datapoint "+id+" found");
                }
                else if(datapointRef.length()>=6&&readable.contains("true")){
                    String[]a=datapointRef.split("\\|");
                    for(int i=0;i<a.length;i++){

                        NamedNodeMap map2 = tag_model.get(a[i]);
                        for (int jj = 0; jj < map2.getLength(); jj++) {
                            if (map2.item(jj).getNodeName().contains("name")) {
                                topic = map2.item(jj).getNodeValue().replaceAll("^\"|\"$", "");
                            }



                        }

                        topic=topic.replaceAll("\\s+","");
                        topic=topic.replace("-","");
                        topic=topic.replace("\\\\\\","");
                        topic=topic.replace("/","");



                        DatapointMap.put(id,new Datapoint(id,devicelist.get(a[i])+"/"+topic,address));

                    }

                }


            }



        }
        for (Map.Entry<String, Datapoint> pair : DatapointMap.entrySet()) {
            Datapoint datapoint = pair.getValue();
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

        if(MQTT){
            MQTT_con.disconnect();
            KNX_con.disconnect();

        }
        else{
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
            if(datapointType.containsKey(datapoint.getGroupAddress())) {
                String type=datapointType.get(datapoint.getGroupAddress());
                if(type.contains("Ein / Aus")||type.contains("Heller / Dunkler")||type.contains("Auf / Zu")||type.contains("Auf / Ab")||type.contains("abrufen / beenden")) {

                    boolean v = KNX_con.readBoolean(datapoint.getGroupAddress());
                    String value = datapoint.getName() + ",device=KNXtoMQTTAMQP value=" +v;
                    if (MQTT) {
                        publishMQTT(datapoint.getTopic(), value);
                        Log.info("MQTT message successful published to "+datapoint.getTopic());
                    } else {
                        publishAMQP(datapoint.getTopic(), value);
                        Log.info("AMQP message successful published to "+datapoint.getTopic());

                    }

                }
                if(type.contains("stetig")) {

                    int v = KNX_con.readUnsigned(datapoint.getGroupAddress());
                    String value = datapoint.getName() + ",device=KNXtoMQTTAMQP value=" +v;
                    if (MQTT) {
                        publishMQTT(datapoint.getTopic(), value);
                        Log.info("MQTT message successful published to "+datapoint.getTopic());
                    } else {
                        publishAMQP(datapoint.getTopic(), value);
                        Log.info("AMQP message successful published to "+datapoint.getTopic());

                    }
                }
                if(type.contains("0% ... 100%")) {

                    int v = KNX_con.readUnsigned(datapoint.getGroupAddress());
                    String value = datapoint.getName() + ",device=KNXtoMQTTAMQP value=" +v;
                    if (MQTT) {
                        publishMQTT(datapoint.getTopic(), value);
                        Log.info("MQTT message successful published to "+datapoint.getTopic());
                    } else {
                        publishAMQP(datapoint.getTopic(), value);
                        Log.info("AMQP message successful published to "+datapoint.getTopic());

                    }
                }

                if(type.contains("16 Bit Fließkommawert")) {

                    String v = KNX_con.readDP(datapoint.getGroupAddress());
                    v=v.replace(" °C","");
                    String value = datapoint.getName()+","+"device=KNXtoMQTTAMQP value="+v;
                    if (MQTT) {
                        publishMQTT(datapoint.getTopic(), value);
                        Log.info("MQTT message successful published to "+datapoint.getTopic());
                    } else {
                        publishAMQP(datapoint.getTopic(), value);
                        Log.info("AMQP message successful published to "+datapoint.getTopic());

                    }
                }

                if(type.contains("8-bit")) {

                    double v = KNX_con.readUnsigned(datapoint.getGroupAddress());
                    String value = datapoint.getName() + ",device=KNXtoMQTTAMQP value=" +v;
                    if (MQTT) {
                        publishMQTT(datapoint.getTopic(), value);
                        Log.info("MQTT message successful published to "+datapoint.getTopic());
                    } else {
                        publishAMQP(datapoint.getTopic(), value);
                        Log.info("AMQP message successful published to "+datapoint.getTopic());
                    }
                }


            }
        }



    }


}