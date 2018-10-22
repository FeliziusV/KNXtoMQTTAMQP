/**
 * Datapoint Class
 *
 * @author Felix Walcher
 * @version 0.1
 */
public class Datapoint {

private String name;
private String topic;
private String groupAdress;
private String dataType;

    public Datapoint(String name,String groupAdress,String dataType){
        this.name=name;
        this.groupAdress=groupAdress;
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

    public String getGroupAdress() {
        return groupAdress;
    }

    public String getDataType() {
        return dataType;
    }
}
