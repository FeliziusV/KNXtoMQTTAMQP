import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public  class AMQP_Communication {
    private final Logger Log = LoggerFactory.getLogger(Main.class);
    private Channel channel;
    Connection con;

    /**
     * AMQP Communication Constructor
     * @param userName     Username of the AMQP broker
     * @param password           Qos of the connection
     * @param virtualHost
     * @param hostName
     * @param portNumber
     * @param topic list of topics
     * @exception IoT_Connection_Exception   if an Connection error occurs
     */
    public AMQP_Communication(String userName, String password, String virtualHost, String hostName, int portNumber, ArrayList<String> topic ) throws IoT_Connection_Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        factory.setPort(portNumber);
        try {
            con = factory.newConnection();
            Log.info("to AMQP Broker connected");
            channel = con.createChannel();
            for(int i=0; i<topic.size();i++) {
                Log.info(topic.get(i));
                channel.exchangeDeclare("ex"+topic.get(i), "direct", true);
                channel.queueDeclare(topic.get(i), true, false, false, null);
                channel.queueBind(topic.get(i), "ex"+topic.get(i), "key");
            }

        }
        catch (IOException e1){
            Log.info("IO Error");
            throw new IoT_Connection_Exception("IO Error");
        }
        catch (TimeoutException e2){
            Log.info("Connection to AMQP Broker failed");
            throw new IoT_Connection_Exception("TimeoutException");
        }

    }
    /**
     * method to publish a message to the AMQP server
     * @param message
     * @param exchangeName
     * @param bindingKey
     * @exception IoT_Connection_Exception   if an Connection error occurs
     */
    public void publishMessage(String message, String exchangeName, String bindingKey) throws IoT_Connection_Exception {
        try {
            System.out.println("-"+exchangeName+"-"+bindingKey+"-");
            channel.basicPublish(exchangeName, bindingKey, null, message.getBytes());
            Log.info("Messge successfully published");
        }
        catch (IOException e1){
            Log.info("Message publish faild");
            throw new IoT_Connection_Exception("IOException");
        }

    }

    /**
     * method to disconnect from an AMQP server
     *
     * @exception IoT_Connection_Exception   if an Connection error occurs
     */
    public void disconnect() throws IoT_Connection_Exception {
        try {
            channel.close();
            con.close();
            Log.info("From AMQP Broker disconnected");
        }
        catch (IOException e1){
            Log.info("IO Error");
            throw new IoT_Connection_Exception("IO Error");        }
        catch (TimeoutException e2){
            Log.info("Disconnection from AMQP Broker failed");
            throw new IoT_Connection_Exception("Disconnection from AMQP Broker failed");
        }
    }





}

