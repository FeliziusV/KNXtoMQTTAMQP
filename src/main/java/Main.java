/**
 * Main Class
 *
 * @author Felix Walcher
 * @version 0.1
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import  Exception.*;
import org.w3c.dom.NamedNodeMap;


public class Main {
    private final Logger Log = LoggerFactory.getLogger(Main.class);
    public static boolean run = true;


    public static void main(String[] args) {

        //xml reader to read the tag based model

        XMLReader xmlReader = new XMLReader();

        try {
            HashMap<String, NamedNodeMap> tag_model = xmlReader.readConfig("knx_input_model.xml");

            PropertiesManager prop = new PropertiesManager("KNXtoMQTTAMQP.config");
            DatapointManager datapointManager = new DatapointManager(tag_model, prop);
            datapointManager.setUpConnection();

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


            //thread to read Data from datapoints

            /*
            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {


                        try {
                            datapointManager.readDatapoints();
                        } catch (IoT_Connection_Exception e1) {

                            return;
                        } catch (KNX_Connection_Exception e3) {
                            return;
                        }
                        if(!run){
                            exec.shutdown();
                        }


                    }

            }, 0, 1, TimeUnit.SECONDS);
        */
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
            System.out.println("kot");
            datapointManager.disconnect();
            return;
        } catch (Invalid_input_Exception e1) {

            return;

        } catch (IoT_Connection_Exception e2) {

            return;

        } catch (KNX_Connection_Exception e3) {

            return;
        }
    }

}



