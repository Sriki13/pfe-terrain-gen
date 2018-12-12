package pfe.terrain.generatorService.parser;

import com.google.gson.Gson;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.generatorService.holder.Algorithm;
import pfe.terrain.generatorService.holder.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {

    public String parseKeys(List<Parameter> params) {
        List<Map<String, String>> maps = new ArrayList<>();

        for (Parameter param : params) {
            Map<String, String> map = new HashMap<>();
            map.put("name", param.getKey().getId());
            map.put("type", param.getKey().getType().getSimpleName());
            map.put("descr", param.getDescription());
            map.put("for", param.getContractName());
            map.put("label", param.getLabel());
            maps.add(map);
        }


        Gson gson = new Gson();
        return gson.toJson(maps);
    }

    public String parseMap(Map<String, Object> map) {
        return new Gson().toJson(map);
    }

    public String parseContext(Context context){
        return parseMap(contextToMap(context));
    }

    public String parseAlgo(List<Algorithm> algorithms) {
        return new Gson().toJson(algorithms);
    }

    public String exceptionToJson(Exception e){
        Map<String,String> map = new HashMap<>();

        map.put("error",e.getMessage());

        return new Gson().toJson(map);
    }

    private Map<String, Object> contextToMap(Context context) {
        Map<String, Object> map = new HashMap<>();

        for (Param key : context.getProperties().keySet()) {
            try {
                map.put(key.getId(), context.getParamOrDefault(key));
            } catch (Exception e) {
                System.err.println("can't put key " + key.getId() + "into map");
            }
        }

        return map;
    }

}
