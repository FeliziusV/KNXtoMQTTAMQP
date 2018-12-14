KNXtoMQTT/AMQP v.0.1:
Before the use of the “KNXtoMQTT/AMQP” application, the speci?ed IoT broker need to run and the KNXnet/IP Gateway has to be reachable. After this the “KNXtoMQTTAMQP.con?g” ?le in the
 root Folder of the application has to be de?ned. In order to use the application correctly, all parameters have to be ?lled in. At the moment, the application supports AMQP and MQTT
 for the IoT communication. Furthermore, the connecting KNX system has to be exported from a ETS5 project with the Web Service Exporter and saved as knx_input_model.xml. After this,
 the “main” class can be executed.

By using this application, IoT devices are able to change KNX datapoint-values over AMQP or MQTT. This feature allows IoT devices to change a status easily, for instance switching a
 light on/o? just with the change of a simple boolean value. In the AMQP mode, the IoT device has to send a message to the “AMQP_input” queue and in the MQTT mode the IoT device has
 to send the message to the “AMQP_input” topic. The message consists of 3 necessary parts. It has to contain the KNX datapoint address, the data value type and the data value.
 This three parameters have to be separated with semicolons. A valid example: ”1/1/2,boolean,true”. This example will change the datapoint boolean value with the groupAddress ”1/1/2” 
to true. At the moment only double, String and boolean are supported.

To close the application press "q"