/**
 * Main Class
 *
 * @author
 * @version 0.1
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Scanner;
import  Exception.*;
import org.w3c.dom.NamedNodeMap;


public class Main {
    private final Logger Log = LoggerFactory.getLogger(Main.class);
    public static boolean run = true;


    public static void main(String[] args) {

        //xml reader to read the tag based model
        System.out.println("###################################");
        System.out.println("#      KNXtoMQTT/AMQP v. 0.1      #");
        System.out.println("###################################");
        System.out.println("note: press 'q' to exit ");
        System.out.println("");

        XMLReader xmlReader = new XMLReader();

        try {


            HashMap<String, NamedNodeMap> tag_model = xmlReader.readConfig("knx_input_model.xml");

            PropertiesManager prop = new PropertiesManager("KNXtoMQTTAMQP.config");
            DatapointManager datapointManager = new DatapointManager(tag_model, prop);
            datapointManager.setUpConnection();


            // Terminal Thread
            Thread console_thread=new Thread(){
                public void run() {
                    Scanner scanner = new Scanner(System.in);

                    while (true) {

                        System.out.print("Enter q to close the application");
                        String input = scanner.nextLine();

                        if ("q".equals(input)) {
                            System.out.println("exiting");
                            run=false;
                            break;
                        }


                    }

                    scanner.close();

                }

            };
            console_thread.start();
            //Thread that reads datapoint-values each seconds
            while(run){
                try {
                    datapointManager.readDatapoints();
                    Thread.sleep(1000);
                } catch (IoT_Connection_Exception e1) {

                    return;
                } catch (KNX_Connection_Exception e3) {
                    return;
                }
                catch (InterruptedException e4){

                }

            }
            datapointManager.disconnect();
            System.exit(0);
        } catch (Invalid_input_Exception e1) {

            return;

        } catch (IoT_Connection_Exception e2) {

            return;

        } catch (KNX_Connection_Exception e3) {

            return;
        }





/*
        try {
            KNX_Communication c = new KNX_Communication("169.254.146.146", "169.254.232.243");

            System.out.println(c.readSwitch("1/1/1"));
        } catch (KNX_Connection_Exception e) {

        }

*/

    }
}



