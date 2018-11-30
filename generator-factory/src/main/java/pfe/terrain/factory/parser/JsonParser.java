package pfe.terrain.factory.parser;

import com.google.gson.Gson;
import pfe.terrain.factory.holder.Algorithm;

import java.util.List;

public class JsonParser {

    public String algoListToJson(List<Algorithm> algorithms){
        return new Gson().toJson(algorithms);
    }

    public String stringToJson(String toParse){
        return new Gson().toJson(toParse);
    }
}
