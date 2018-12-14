/**
 * Datapoint Class
 *
 * @author
 * @version 0.1
 */


//representation of a KNX datapoint
public class Datapoint {

    private String name;
    private String topic;
    private String groupAddress;


    public Datapoint(String name, String topic, String groupAddress){
        this.name=name;
        this.topic=topic;
        this.groupAddress=groupAddress;

    }

    public String getName(){
        return name;
    }

    public String getTopic(){
        return topic;
    }

    public String getGroupAddress() {
        return groupAddress;
    }


}