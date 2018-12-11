package pfe.terrain.factory.parser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.entities.Composition;
import pfe.terrain.factory.exception.ParsingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {

    public String algoListToJson(List<Algorithm> algorithms){
        Gson gson = new Gson();

        JsonArray obj = new JsonArray();

        for(Algorithm algorithm : algorithms){
            JsonObject element = new JsonObject();
            element.add("id",gson.toJsonTree(algorithm.getId()));

            element.add("contracts",
                    gson.toJsonTree(gson.fromJson(algorithm.getContract().toJson(),Map.class),Map.class));

            obj.add(element);
        }

        return gson.toJson(obj);
    }

    public String exceptionToJson(Exception e){

        Map<String,String> amp = new HashMap<>();

        amp.put("error",e.getMessage());

        return new Gson().toJson(amp);
    }

    public String compoToJson(List<Composition> compositions){
        List<Map<String,Object>> lists = new ArrayList<>();

        for(Composition compo : compositions){
            Map<String,Object> map = new HashMap<>();

            map.put("name",compo.getName());
            lists.add(map);
        }

        return new Gson().toJson(lists);
    }

    public List<String> listFromJson(String json){
        return new Gson().fromJson(json,List.class);
    }

    public String okAnswer(){
        Map map = new HashMap();

        map.put("status", "OK");

        return new Gson().toJson(map);
    }

    public AlgoCompatibilityChange getAlgoCompatibility(String json) throws ParsingException {
        try{
            Gson gson = new Gson();
            JsonObject obj = gson.toJsonTree(gson.fromJson(json,Map.class),Map.class).getAsJsonObject();
            JsonArray jsonNames = obj.getAsJsonArray("AlgoNames");

            List<String> names = new ArrayList<>();

            for(JsonElement name : jsonNames){
                names.add(name.getAsString());
            }

            return new AlgoCompatibilityChange(names,obj.get("compatibility").getAsInt());

        }catch (Exception e){
            throw new ParsingException();
        }
    }
}
