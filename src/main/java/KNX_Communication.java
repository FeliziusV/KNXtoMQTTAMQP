import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuwien.auto.calimero.GroupAddress;

import tuwien.auto.calimero.KNXException;
import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.knxnetip.Discoverer;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

import java.net.InetSocketAddress;

public  class KNX_Communication{

    private final Logger Log = LoggerFactory.getLogger(Main.class);


    private static final InetSocketAddress local = new InetSocketAddress("169.254.232.243", 0);

    public KNX_Communication(String gatewayIP) {
        String group="1/1/2";
        final InetSocketAddress remote = new InetSocketAddress(gatewayIP, 3671);

        // Create our network link, and pass it to a process communicator

        try (KNXNetworkLink knxLink = KNXNetworkLinkIP.newTunnelingLink(local, remote, false, TPSettings.TP1);

             ProcessCommunicator pc = new ProcessCommunicatorImpl(knxLink)) {
            GroupAddress g=new GroupAddress(group);
           pc.write(g,true);
           boolean value=pc.readBool(new GroupAddress("1/1/32"));

            //boolean value=pc.readBool(new GroupAddress(group));




            System.out.println("datapoint " + group + " value ="+value);



            // Uncomment the next line, if you want to write back the same value to the KNX network

            // pc.write(group, value);

        }

        catch (KNXException | InterruptedException e) {

            System.out.println("Error accessing KNX datapoint: " + e.getMessage());

        }


    }
    public void disconnect(){

    }

    public String readDatapoint(String x){
        return "X";
    }










}