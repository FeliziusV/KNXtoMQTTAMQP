import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public  class MQTT_Communication {

    private final Logger Log = LoggerFactory.getLogger(Main.class);
    private MqttClient client;
    private int qos;


    /**
     * MQTT Communication Constructor
     * @param serverURI     Server URI of the Broker
     * @param qos           Qos of the connection
     * @exception IoT_Connection_Exception   if an Connection error occurs
     */

    public MQTT_Communication(String serverURI,int qos) throws IoT_Connection_Exception {
        this.qos=qos;
        try {
            client = new MqttClient(serverURI, MqttClient.generateClientId());
            client.connect();
            Log.info("to MQTT broker connected");

        }
        catch (MqttException e){
            Log.error("MQTT Connection Error");
            throw new IoT_Connection_Exception("MqttException");
        }
    }
    /**
     * method to publish a Message to a MQTT Broker
     * @param topic
     * @param message
     * @exception IoT_Connection_Exception   if an Connection error occurs
     */
    public void publishMessage(String topic, String message)throws IoT_Connection_Exception {
        MqttMessage msg = new MqttMessage();
        msg.setPayload(message.getBytes());
        msg.setQos(qos);
        try {
            client.publish(topic, msg);
            Log.info("message successfully sent");

        }
        catch (MqttException e){
            Log.error("unable to send the Message to MQTT Broker");
            throw new IoT_Connection_Exception("MqttException");
        }

    }
    /**
     * method to disconnect from a MQTT broker
     * @exception IoT_Connection_Exception   if an Connection error occurs
     */
    public void disconnect()throws IoT_Connection_Exception {
        try {
            client.disconnect();
            Log.info("to MQTT broker disconnected");
        }
        catch(MqttException e){
            throw new IoT_Connection_Exception("MqttException");
        }

    }

}



