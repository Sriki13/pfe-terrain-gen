package pfe.terrain.generatorService.controller;

import pfe.terrain.gen.ChocoDependencySolver;
import pfe.terrain.gen.FinalContract;
import pfe.terrain.gen.MapGenerator;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.MapContext;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.algo.parsing.ContextParser;
import pfe.terrain.gen.exception.InvalidContractException;
import pfe.terrain.gen.exception.MissingRequiredException;
import pfe.terrain.gen.exception.UnsolvableException;
import pfe.terrain.generatorService.graph.GraphGenerator;
import pfe.terrain.generatorService.holder.Parameter;
import pfe.terrain.generatorService.reflection.ContractReflection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceController {


    private Generator generator;
    private Context context;

    public ServiceController() throws InvalidContractException, UnsolvableException, MissingRequiredException {
        ContractReflection reflection = new ContractReflection();
        List<Contract> contracts = reflection.getContracts();

        ChocoDependencySolver solver = new ChocoDependencySolver(contracts, contracts, new FinalContract());
        this.generator = new MapGenerator(solver.orderContracts());
    }

    public ServiceController(Generator generator) {
        this.generator = generator;
    }

    public String execute() {
        return this.generator.generate();
    }

    public Map<String,Object> setContext(String contextString) {
        ContextParser parser = new ContextParser(contextString);

        this.context = new MapContext(parser.getMap(), this.generator.getContracts());
        generator.setParams(this.context);

        Map<String,Object> map = new HashMap<>();

        for(Key key : this.context.getProperties().keySet()){
            try {
                map.put(key.getId(), this.context.getProperty(key));
            } catch (Exception e){
                System.err.println("can't put key " + key.getId() + "into map");
            }
        }

        return map;
    }

    public List<Parameter> getParameters(){
        List<Parameter> keys = new ArrayList<>();

        for(Contract contract : this.generator.getContracts()){
            for(Key key : contract.getRequestedParameters()){
                keys.add(new Parameter(key,contract.getName()));
            }
        }

        return keys;
    }

    public Context getContext() {
        return this.context;
    }

    public String getGraph() {
        GraphGenerator graphGenerator = new GraphGenerator(generator.getContracts());
        graphGenerator.generateGraph();
        return graphGenerator.exportAsJSON();
    }

    public void generateGraphImage() throws IOException {
        GraphGenerator graphGenerator = new GraphGenerator(generator.getContracts());
        graphGenerator.generateGraph();
        graphGenerator.exportAsPNG("graph.png");
    }

}
