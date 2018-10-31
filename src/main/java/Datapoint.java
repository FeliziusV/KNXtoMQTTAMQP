/**
 * Datapoint Class
 *
 * @author Felix Walcher
 * @version 0.1
 */


//representation of a KNX datapoint
public class Datapoint {

private String name;
private String topic;
private String groupAddress;
private String dataType;

    public Datapoint(String name, String groupAddress, String dataType){
        this.name=name;
        this.groupAddress = groupAddress;
        this.dataType=dataType;
        }

        public String getName(){
        return name;
        }

        public void setTopic(String topic){
        this.topic=topic;
        }

        public String getTopic(){
        return topic;
        }

    public String getGroupAddress() {
        return groupAddress;
    }

    public String getDataType() {
        return dataType;
    }
}
