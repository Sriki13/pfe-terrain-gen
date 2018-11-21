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

    public List<Integer> getGeneratorList(){
        List<Integer> ids = new ArrayList<>();

        for(Generator gen : generators){
            ids.add(gen.getId());
        }

        return ids;
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
