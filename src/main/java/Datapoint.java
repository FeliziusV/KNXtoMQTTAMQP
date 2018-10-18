/**
 * Datapoint Class
 *
 * @author Felix Walcher
 * @version 0.1
 */
public class Datapoint {

private String name;
private String topic;

    public Datapoint(String name){
        this.name=name;
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




}
