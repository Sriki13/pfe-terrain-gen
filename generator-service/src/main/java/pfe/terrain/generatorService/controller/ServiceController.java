package pfe.terrain.generatorService.controller;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.MapContext;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.algo.parsing.ContextParser;
import pfe.terrain.generatorService.GeneratorLoader;
import pfe.terrain.generatorService.GeneratorRunner;
import pfe.terrain.generatorService.exception.NoSuchGenerator;
import pfe.terrain.generatorService.parser.AnswerParser;
import pfe.terrain.generatorService.parser.LazyContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceController {

    private static Map<Integer, Context> contextMap;

    private GeneratorRunner runner;

    public ServiceController() throws IOException {
        contextMap = new HashMap<>();
        GeneratorLoader generatorLoader = new GeneratorLoader();


        this.runner = new GeneratorRunner(generatorLoader.load());
    }

    public ServiceController(List<Generator> generators){
        contextMap = new HashMap<>();
        this.runner = new GeneratorRunner(generators);
    }

    public String getGenList(){
        return AnswerParser.intListToJson(runner.getGeneratorList());
    }

    public String executeById(int id) throws NoSuchGenerator {
        if(contextMap.containsKey(id)){
            return runner.executebyIdWithContext(id,contextMap.get(id));
        }
        return runner.executeById(id);
    }

    public void setContext(int id, String contextString){
        ContextParser parser = new ContextParser(contextString);

        Context context = new LazyContext(parser.getMap());

        contextMap.put(id,context);
    }

    public Map<Integer,Context> getContextMap(){
        return contextMap;
    }
}
