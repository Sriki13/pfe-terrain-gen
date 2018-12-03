package pfe.terrain.factory.parser;

import com.google.gson.Gson;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.entities.Composition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {

    public String algoListToJson(List<Algorithm> algorithms){
        return new Gson().toJson(algorithms);
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
}
