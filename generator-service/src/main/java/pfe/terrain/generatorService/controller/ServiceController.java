package pfe.terrain.generatorService.controller;

import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.generatorService.GeneratorLoader;
import pfe.terrain.generatorService.GeneratorRunner;
import pfe.terrain.generatorService.exception.NoSuchGenerator;
import pfe.terrain.generatorService.parser.AnswerParser;

import java.io.IOException;
import java.util.List;

public class ServiceController {

    private GeneratorRunner runner;

    public ServiceController() throws IOException {
        GeneratorLoader generatorLoader = new GeneratorLoader();


        this.runner = new GeneratorRunner(generatorLoader.load());
    }

    public ServiceController(List<Generator> generators){
        this.runner = new GeneratorRunner(generators);
    }

    public String getGenList(){
        return AnswerParser.intListToJson(runner.getGeneratorList());
    }

    public String executeById(int id) throws NoSuchGenerator {
        return runner.executeById(id);

    }
}
