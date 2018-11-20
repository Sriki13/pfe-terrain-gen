package pfe.terrain.generatorService;

import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.generatorService.exception.NoSuchGenerator;

import java.util.ArrayList;
import java.util.List;

public class GeneratorRunner {
    private List<Generator> generators;


    public GeneratorRunner(List<Generator> generators){
        this.generators = new ArrayList<>();
        this.generators.addAll(generators);
    }

    public String getGeneratorList(){
        StringBuilder builder = new StringBuilder();

        for(Generator gen : generators){
            builder.append(gen.getId());
            builder.append("\n");
        }

        return builder.toString();
    }

    public String executeById(int id) throws NoSuchGenerator{
        for(Generator gen : generators){
            if(gen.getId() == id){
                return gen.generate();
            }
        }
        throw new NoSuchGenerator();
    }
}
