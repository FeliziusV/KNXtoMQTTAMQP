import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.KNXException;
import tuwien.auto.calimero.KNXFormatException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;
import  Exception.*;


import java.net.InetSocketAddress;

public  class KNX_Communication {

    private final Logger Log = LoggerFactory.getLogger(Main.class);
    private ProcessCommunicator pc;
    private KNXNetworkLink knxLink;

    /**
     * AMQP Communication Constructor
     * @param gatewayIP     KNX Gateway IP
     * @param localIP       local IP

     * @exception KNX_Connection_Exception   if an Connection error occurs
     */
    public KNX_Communication(String gatewayIP, String localIP) throws KNX_Connection_Exception{

        final InetSocketAddress remote = new InetSocketAddress(gatewayIP, 3671);

        InetSocketAddress local = new InetSocketAddress(localIP, 0);
        try {
            knxLink = KNXNetworkLinkIP.newTunnelingLink(local, remote, false, TPSettings.TP1);
            pc = new ProcessCommunicatorImpl(knxLink);


        } catch (KNXException | InterruptedException e) {

           Log.info("Error accessing KNX datapoint: " + e.getMessage());
           throw new KNX_Connection_Exception(e.getMessage());

        }


    }

    /**
     * Methode to read double values from a KNX groupAddresses
     *
     * @param groupAddress  groupAddress of the KNX component
     * @exception KNX_Connection_Exception  if an Connection error occurs
     */

    public double readDouble(String groupAddress) throws KNX_Connection_Exception {
        double value=0;
        try {
            Log.info("reading double value from datapoint " + groupAddress);

                 value= pc.readFloat(new GroupAddress(groupAddress));

            Log.info("datapoint " + groupAddress + " value = " + value);

        } catch (KNXFormatException e1) {
            throw new KNX_Connection_Exception(e1.getMessage());


        }
        catch (KNXException e2){
            throw new KNX_Connection_Exception(e2.getMessage());


        }
        catch (InterruptedException e3){
            throw new KNX_Connection_Exception(e3.getMessage());

        }
        return value;
}

    /**
     * Methode to read boolean values from a KNX groupAddresses
     *
     * @param groupAddress  groupAddress of the KNX component
     * @exception KNX_Connection_Exception  if an Connection error occurs
     */
public boolean readBoolean(String groupAddress) throws KNX_Connection_Exception{
        boolean value=false;
        try{
            value=pc.readBool(new GroupAddress(groupAddress));
        }
        catch (KNXException e1){
            throw new KNX_Connection_Exception(e1.getMessage());
        }
        catch (InterruptedException e2){
            throw new KNX_Connection_Exception(e2.getMessage());

        }
        return value;

}
    /**
     * Methode to write boolean values from a KNX groupAddress
     *
     * @param groupAddress  groupAddress of the KNX component
     * @value  boolean value
     * @exception KNX_Connection_Exception  if an Connection error occurs
     */
public void writeBoolean(String groupAddress, boolean value) throws  KNX_Connection_Exception{
    try{
       pc.write(new GroupAddress(groupAddress),value);

    }
    catch (KNXException e){
        throw new KNX_Connection_Exception(e.getMessage());

    }

}
    /**
     * Methode to write boolean values from a KNX groupAddress
     *
     * @param groupAddress  groupAddress of the KNX component
     * @value  double value
     * @exception KNX_Connection_Exception  if an Connection error occurs
     */
public void writeDouble(String groupAddress, double value) throws  KNX_Connection_Exception{


        try{
            pc.write(new GroupAddress(groupAddress),(float)value,true);

        }
        catch (KNXException e){
            throw new KNX_Connection_Exception(e.getMessage());

        }
}



    /**
     * method to disconnect from the KNX Gateway
     *
     */

public void disconnect() {
        pc.close();
        knxLink.close();

    }










}