package pfe.terrain.generatorService.controller;

import pfe.terrain.gen.DependencySolver;
import pfe.terrain.gen.MapGenerator;
import pfe.terrain.gen.algo.Generator;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.context.MapContext;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.parsing.ContextParser;
import pfe.terrain.gen.algo.reflection.ContractReflection;
import pfe.terrain.gen.constraints.AdditionalConstraint;
import pfe.terrain.gen.exception.DuplicatedProductionException;
import pfe.terrain.gen.exception.MissingRequiredException;
import pfe.terrain.gen.exception.MultipleEnderException;
import pfe.terrain.gen.exception.UnsolvableException;
import pfe.terrain.gen.parser.ParamParser;
import pfe.terrain.generatorService.graph.GraphGenerator;
import pfe.terrain.generatorService.holder.Algorithm;
import pfe.terrain.generatorService.holder.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceController {

    private Generator generator;
    private Context recessive;
    private Context dominant;
    private List<AdditionalConstraint> constraints;

    public ServiceController() throws UnsolvableException,
            MissingRequiredException, DuplicatedProductionException, MultipleEnderException {
        ContractReflection reflection = new ContractReflection();
        List<Contract> contracts = reflection.getContracts();

        this.recessive = new Context();

        ParamParser initializer = new ParamParser();
        this.dominant = initializer.getContext(contracts);

        DependencySolver solver = new DependencySolver(contracts);
        this.constraints = initializer.getConstraints(contracts);

        this.generator = new MapGenerator(solver.orderContracts(this.listToArray(this.constraints)));
        this.generator.setParams(this.dominant.merge(this.recessive));
    }

    public ServiceController(Generator generator) throws UnsolvableException,
            MissingRequiredException, DuplicatedProductionException, MultipleEnderException{
        this();
        this.generator = generator;
        this.generator.setParams(this.dominant.merge(this.recessive));
    }

    public void execute() {
        this.generator.generate();
    }

    public Object getProperty(String keyId) {
        return this.generator.getProperty(keyId);
    }

    public Map<String, Object> setContext(String contextString) {

        ContextParser parser = new ContextParser(contextString);
        this.recessive = new MapContext(parser.getMap(), this.generator.getContracts());
        Context merge = this.dominant.merge(this.recessive);
        generator.setParams(merge);
        return contextToMap(merge);
    }

    public List<Parameter> getParameters() {
        List<Parameter> allParams = new ArrayList<>();
        Map<String, Object> contexts = this.contextToMap(this.dominant);
        for (Contract contract : this.generator.getContracts()) {
            for (Param param : contract.getRequestedParameters()) {
                if (contexts.containsKey(param.getId())) continue;
                allParams.add(new Parameter(param, contract.getName(), param.getDescription(), param.getLabel()));
            }
        }

        return allParams;
    }

    public Map<String, Object> getContextMap() {
        return contextToMap(this.dominant.merge(this.recessive));
    }


    public Context getContext() {
        return this.dominant.merge(this.recessive);
    }

    public String getGraph() {
        GraphGenerator graphGenerator = new GraphGenerator(generator.getContracts());
        graphGenerator.generateGraph();
        return graphGenerator.exportAsSVG();
    }

    public List<Algorithm> getAlgoList() {
        List<Algorithm> algos = new ArrayList<>();

        for (int i = 0; i < this.generator.getContracts().size(); i++) {
            algos.add(new Algorithm(this.generator.getContracts().get(i).getName(), i));
        }

        return algos;
    }


    private Map<String, Object> contextToMap(Context context) {
        Map<String, Object> map = new HashMap<>();

        for (Param key : context.getProperties().keySet()) {
            try {
                //noinspection unchecked
                map.put(key.getId(), context.getParamOrDefault(key));
            } catch (Exception e) {
                System.err.println("can't put key " + key.getId() + " into map");
            }
        }

        return map;
    }

    private AdditionalConstraint[] listToArray(List<AdditionalConstraint> constraints) {
        AdditionalConstraint[] array = new AdditionalConstraint[constraints.size()];

        for (int i = 0; i < constraints.size(); i++) {
            array[i] = constraints.get(i);
        }

        return array;
    }

    public List<String> getConstraintList() {
        List<String> consts = new ArrayList<>();
        for (AdditionalConstraint constraint : this.constraints) {
            consts.add(constraint.getName());
        }

        return consts;
    }

    public byte[] getExecutionChart() {
        return generator.getExecutionChart();
    }
}
