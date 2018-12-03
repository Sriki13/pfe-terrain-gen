package pfe.terrain.factory.parser;

import com.google.gson.Gson;
import pfe.terrain.factory.holder.Algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {

    public String algoListToJson(List<Algorithm> algorithms){
        return new Gson().toJson(algorithms);
    }

    public String stringToJson(String toParse){
        return new Gson().toJson(toParse);
    }
    public String exceptionToJson(Exception e){

        Map<String,String> amp = new HashMap<>();

        amp.put("error",e.getMessage());

        return new Gson().toJson(amp);
    }
}
