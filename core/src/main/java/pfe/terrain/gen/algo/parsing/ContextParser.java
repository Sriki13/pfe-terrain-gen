package pfe.terrain.gen.algo.parsing;

import com.google.gson.Gson;

import java.util.Map;

public class ContextParser {

    private Map<String,Object> map;

    public ContextParser(String jsonContext){
        Gson gson = new Gson();
        this.map = gson.fromJson(jsonContext,Map.class);
    }

    public Map<String,Object> getMap(){
        return this.map;
    }

}
