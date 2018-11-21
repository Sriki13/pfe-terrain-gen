package pfe.terrain.generatorService.parser;

import com.google.gson.Gson;

import java.util.List;

public class AnswerParser {

    public static String intListToJson(List<Integer> ints){
        return new Gson().toJson(ints);
    }
}
