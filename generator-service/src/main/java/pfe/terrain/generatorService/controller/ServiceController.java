package pfe.terrain.generatorService.controller;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import pfe.terrain.gen.ChocoDependencySolver;
import pfe.terrain.gen.FinalContract;
import pfe.terrain.gen.MapGenerator;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.MapContext;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.algo.parsing.ContextParser;
import pfe.terrain.gen.exception.InvalidContractException;
import pfe.terrain.gen.exception.MissingRequiredException;
import pfe.terrain.gen.exception.UnsolvableException;
import pfe.terrain.generatorService.GeneratorLoader;
import pfe.terrain.generatorService.GeneratorRunner;
import pfe.terrain.generatorService.exception.NoSuchGenerator;
import pfe.terrain.generatorService.parser.AnswerParser;
import pfe.terrain.generatorService.parser.LazyContext;

import java.io.IOException;
import java.util.*;

public class ServiceController {


    private Generator generator;
    private Context context;

    public ServiceController() throws InvalidContractException, UnsolvableException, MissingRequiredException {
        GeneratorLoader generatorLoader = new GeneratorLoader();

        List<Contract> contracts = this.getContracts();
        System.out.println(contracts);
        System.out.println(contracts.size());
        ChocoDependencySolver solver = new ChocoDependencySolver(contracts,contracts,new FinalContract());
        this.generator = new MapGenerator(solver.orderContracts());
    }

    public ServiceController(List<Contract> contracts) throws InvalidContractException, UnsolvableException, MissingRequiredException {
        ChocoDependencySolver solver = new ChocoDependencySolver(contracts,contracts,new FinalContract());
        this.generator = new MapGenerator(solver.orderContracts());
    }

    public String execute() {
        return this.generator.generate();
    }

    public void setContext(String contextString){
        ContextParser parser = new ContextParser(contextString);

        generator.setParams(new MapContext(parser.getMap(),this.generator.getContracts()));
        this.context = context;
    }

    public Context getContext(){
        return this.context;
    }

    private List<Contract> getContracts(){
        List<Contract> contracts = new ArrayList<>();
        try {
            Reflections reflections = new Reflections("pfe.terrain.gen", new SubTypesScanner(false));
            Set<Class<? extends Contract>> subTypes = reflections.getSubTypesOf(Contract.class);

            for (Class cl : subTypes) {
                try {
                    contracts.add((Contract) cl.newInstance());
                } catch (InstantiationException e) {
                    System.err.println(cl.getName() + " was not instantiated");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contracts;
    }

}
