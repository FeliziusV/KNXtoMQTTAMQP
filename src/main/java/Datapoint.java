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

    public Datapoint(String name){
        this.name=name;

    }
    public Datapoint(String name, String topic, String groupAddress, String dataType){
        this.name=name;
        this.topic=topic;
        this.groupAddress=groupAddress;
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

    public void setGroupAddress(String groupAddress){
        this.groupAddress=groupAddress;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType){
        this.dataType=dataType;
    }
}