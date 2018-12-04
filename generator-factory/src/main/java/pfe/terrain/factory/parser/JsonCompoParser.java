package pfe.terrain.factory.parser;

import com.google.gson.Gson;
import pfe.terrain.factory.exception.MissingKeyException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonCompoParser {
    private String name;
    private List<String> algoName;
    private String context;

    private String nameKey = "name";
    private String contextKey = "context";
    private String algoKey = "algorithm";

    public JsonCompoParser(String json) throws MissingKeyException{


        try {

            Map<String,Object> map = new Gson().fromJson(json,Map.class);
            if (map.containsKey(this.nameKey)) {
                this.name = (String) map.get(this.nameKey);
            } else {
                throw new MissingKeyException(this.nameKey);
            }

            if (map.containsKey(this.contextKey)) {
                this.context = new Gson().toJson(map.get(this.contextKey));
            } else {
                throw new MissingKeyException(this.contextKey);
            }

            if (map.containsKey(this.algoKey)) {
                this.algoName = (List) map.get(this.algoKey);
            } else {
                throw new MissingKeyException(this.algoKey);
            }
        } catch (MissingKeyException e){
            throw e;
        } catch (Exception e){
            throw new MissingKeyException();
        }

    }

    public String getName() {
        return name;
    }

    public List<String> getAlgoName() {
        return algoName;
    }

    public String getContext() {
        return context;
    }
}
