import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuwien.auto.calimero.GroupAddress;

import tuwien.auto.calimero.KNXException;
import tuwien.auto.calimero.KNXFormatException;
import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.knxnetip.Discoverer;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

import java.net.InetSocketAddress;

public  class KNX_Communication {

    private final Logger Log = LoggerFactory.getLogger(Main.class);
    private ProcessCommunicator pc;
    private KNXNetworkLink knxLink;


    public KNX_Communication(String gatewayIP, String localIP) {

        final InetSocketAddress remote = new InetSocketAddress(gatewayIP, 3671);

        InetSocketAddress local = new InetSocketAddress(localIP, 0);
        try {
            knxLink = KNXNetworkLinkIP.newTunnelingLink(local, remote, false, TPSettings.TP1);
            pc = new ProcessCommunicatorImpl(knxLink);


        } catch (KNXException | InterruptedException e) {

            System.out.println("Error accessing KNX datapoint: " + e.getMessage());

        }


    }

    public void disconnect() {
        pc.close();
        knxLink.close();

    }

    public double readDouble(String groupadress) {
        double value=0;
        try {
            System.out.println("read boolean value from datapoint " + groupadress);

                value = pc.readFloat(new GroupAddress(groupadress));

            System.out.println("datapoint " + groupadress + " value = " + value);

        } catch (KNXFormatException e1) {

        }
        catch (KNXException e2){


        }
        catch (InterruptedException e3){

        }
        return value;
}
public String readString(String groupaddress){
        return  "";
}










}