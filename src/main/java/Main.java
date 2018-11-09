/**
 * Main Class
 *
 * @author Felix Walcher
 * @version 0.1
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import  Exception.*;



public class Main {
    private final Logger Log = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {

        //xml reader to read the tag based model
        XMLReader xmlReader = new XMLReader();

      try {
          HashMap<String, Entity_DTO> tag_model = xmlReader.readConfig("knx_input_model.xml");

          PropertiesManager prop = new PropertiesManager("KNXtoMQTTAMQP.config");
          DatapointManager datapointManager = new DatapointManager(tag_model, prop);
          datapointManager.setUpConnection();


            //thread to read Data from datapoints
          ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
          exec.scheduleAtFixedRate(new Runnable() {
              @Override
              public void run() {
                  try {
                      datapointManager.readDatapoints();
                  }
                  catch (IoT_Connection_Exception e1) {

                      return;
                  }
                  catch ( KNX_Connection_Exception e3){
                      return;
                  }

              }
          }, 0, 1, TimeUnit.SECONDS);



          datapointManager.disconnect();
      }
      catch (Invalid_input_Exception e1) {

        return;
        }

      catch (IoT_Connection_Exception e2){
          return;

      }

      catch ( KNX_Connection_Exception e3){
          return;
      }


}








    }



