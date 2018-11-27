package pfe.terrain.generatorService.controller;

import pfe.terrain.gen.DependencySolver;
import pfe.terrain.gen.FinalContract;
import pfe.terrain.gen.MapGenerator;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.MapContext;
import pfe.terrain.gen.algo.Param;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.algo.parsing.ContextParser;
import pfe.terrain.gen.exception.DuplicatedProductionException;
import pfe.terrain.gen.exception.InvalidContractException;
import pfe.terrain.gen.exception.MissingRequiredException;
import pfe.terrain.gen.exception.UnsolvableException;
import pfe.terrain.generatorService.graph.GraphGenerator;
import pfe.terrain.generatorService.holder.Algorithm;
import pfe.terrain.generatorService.holder.Parameter;
import pfe.terrain.generatorService.initializer.ContextInitializer;
import pfe.terrain.generatorService.reflection.ContractReflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceController {


    private Generator generator;
    private Context recessive;
    private Context dominant;

    public ServiceController() throws InvalidContractException, UnsolvableException, MissingRequiredException, DuplicatedProductionException {
        ContractReflection reflection = new ContractReflection();
        List<Contract> contracts = reflection.getContracts();

        this.recessive = new Context();

        ContextInitializer initializer = new ContextInitializer();
        this.dominant = initializer.getContext(contracts);

        DependencySolver solver = new DependencySolver(contracts, contracts, new FinalContract());
        this.generator = new MapGenerator(solver.orderContracts());
    }

    public ServiceController(Generator generator) {
        this.generator = generator;
    }

    public String execute() {
        return this.generator.generate();
    }

    public Map<String, Object> setContext(String contextString) {

        ContextParser parser = new ContextParser(contextString);

        this.recessive = new MapContext(parser.getMap(), this.generator.getContracts());

        Context merge = this.dominant.merge(this.recessive);

        generator.setParams(merge);

        return contextToMap(merge);
    }

    public List<Parameter> getParameters() {
        List<Parameter> keys = new ArrayList<>();

        for (Contract contract : this.generator.getContracts()) {
            for (Param key : contract.getRequestedParameters()) {
                keys.add(new Parameter(key, contract.getName(), key.getDescription()));
            }
        }

        return keys;
    }

    public Map<String,Object> getContextMap() {
        return contextToMap(this.dominant.merge(this.recessive));
    }


    public Context getContext() {
        return this.dominant.merge(this.recessive);
    }

    public String getGraph() {
        GraphGenerator graphGenerator = new GraphGenerator(generator.getContracts());
        graphGenerator.generateGraph();
        return graphGenerator.exportAsJSON();
    }

    public List<Algorithm> getAlgoList() {
        List<Algorithm> algos = new ArrayList<>();

        for (int i = 0; i < this.generator.getContracts().size(); i++) {
            algos.add(new Algorithm(this.generator.getContracts().get(i).getName(), i));
        }

        return algos;
    }



    private Map<String,Object> contextToMap(Context context){
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
