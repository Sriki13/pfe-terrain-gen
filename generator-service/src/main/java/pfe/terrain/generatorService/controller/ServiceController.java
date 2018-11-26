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
import pfe.terrain.generatorService.reflection.ContractReflection;

import java.io.IOException;
import java.util.*;

public class ServiceController {


    private Generator generator;
    private Context context;

    public ServiceController() throws InvalidContractException, UnsolvableException, MissingRequiredException {
        ContractReflection reflection = new ContractReflection();
        List<Contract> contracts = reflection.getContracts();
        System.out.println(contracts);
        System.out.println(contracts.size());
        ChocoDependencySolver solver = new ChocoDependencySolver(contracts,contracts,new FinalContract());
        this.generator = new MapGenerator(solver.orderContracts());
    }

    public ServiceController(Generator generator) throws InvalidContractException, UnsolvableException, MissingRequiredException {
        this.generator = generator;
    }

    public String execute() {
        return this.generator.generate();
    }

    public void setContext(String contextString){
        ContextParser parser = new ContextParser(contextString);

        this.context = new MapContext(parser.getMap(),this.generator.getContracts());
        generator.setParams(this.context);
    }

    public Context getContext(){
        return this.context;
    }

}
