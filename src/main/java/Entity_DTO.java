/**
 * Entity Class
 *
 * @author Felix Walcher
 * @version 0.1
 */



import java.util.HashMap;

public class Entity_DTO {

    private HashMap<String, String> features = new HashMap<String, String>();

    public Entity_DTO(){}

    public void addFeature(String name, String value) {
        features.put(name, value);
    }

    public boolean containsFeature(String name) {
        if (features.containsKey(name)) {
            return true;
        }
        return false;
    }

    public String getFeature(String name) {
        return features.get(name);
    }

}