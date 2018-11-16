import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MQTT_Listener implements MqttCallback {
    KNX_Communication knx_con;


    private final Logger Log = LoggerFactory.getLogger(Main.class);


    public void subscribe(String topic, String brokerUrl,KNX_Communication knx_con) {
        this.knx_con=knx_con;

        MemoryPersistence persistence = new MemoryPersistence();

        try {

            MqttClient sampleClient = new MqttClient(brokerUrl, "KNXtoMQTT/AMQP", persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);



           Log.info("Mqtt Connecting to broker: " + brokerUrl);
            sampleClient.connect(connOpts);
            Log.info("Mqtt Connected");

            sampleClient.setCallback(this);
            sampleClient.subscribe(topic);

            Log.info("Subscribed");
            Log.info("Listening");

        } catch (MqttException me) {

            Log.info("Mqtt reason " + me.getReasonCode());
            Log.info("Mqtt msg " + me.getMessage());
            Log.info("Mqtt loc " + me.getLocalizedMessage());
            Log.info("Mqtt cause " + me.getCause());
            Log.info("Mqtt excep " + me);
        }
    }


    public void connectionLost(Throwable arg0) {

    }

    public void deliveryComplete(IMqttDeliveryToken arg0) {

    }


    public void messageArrived(String topic, MqttMessage message) throws Exception {


        Log.info("Mqtt msg : " + message.toString());
        String m=message.toString();
        String s[]=m.split(";");
        String groupAddress=s[0];
        String datatype=s[1];
        String value=s[2];

        if(datatype.contains("boolean")) {


            knx_con.writeBoolean(groupAddress,Boolean.parseBoolean(value));
        }
        else {


            knx_con.writeDouble(groupAddress,Double.parseDouble(value));
        }
    }

}