import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class AMQP_Listener {
    private final Logger Log = LoggerFactory.getLogger(Main.class);



    public AMQP_Listener(String userName, String password, String virtualHost, String hostName, int portNumber, String topic, KNX_Communication knx_con) {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        factory.setPort(portNumber);
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();


            channel.queueDeclare(topic, false, false, false, null);
            Consumer consumer = new DefaultConsumer(channel) {

                @Override

                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)

                        throws IOException {

                    String message = new String(body, "UTF-8");

                    Log.info("AMQP Input: '" + message + "'");

                    String s[]=message.split(";");
                    String groupAddress=s[0];
                    String datatype=s[1];
                    String value=s[2];

                    if(datatype.contains("boolean")) {
                        try {

                            knx_con.writeBoolean(groupAddress, Boolean.parseBoolean(value));
                        }
                        catch ( Exception e2){
                           Log.error("KNX writing error");
                        }
                    }
                    else if(datatype.contains("String")) {
                        try {

                            knx_con.writeString(groupAddress, value);
                        }
                        catch ( Exception e2){
                            Log.error("KNX writing error");

                        }
                    }
                    else if (datatype.contains("double")){
                        try {


                            knx_con.writeDouble(groupAddress, Double.parseDouble(value));
                        }
                        catch ( Exception e2){
                            Log.error("KNX writing error");

                        }
                    }


                }

            };

            channel.basicConsume(topic, true, consumer);


        }
        catch (IOException e1){
            Log.error("AMQP listening Error");


        }
        catch (Exception e2){
            Log.error("AMQP listening Error");

        }

    }


}
