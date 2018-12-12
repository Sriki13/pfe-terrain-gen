package pfe.terrain.generatorService;

import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.generatorService.graph.GraphGenerator;
import pfe.terrain.gen.algo.reflection.ContractReflection;

import java.util.List;

public class GraphMain {

    public static void main(String[] args) throws Exception {
        System.out.println("Generating pfe.terrain.generatorService.graph...");
        ContractReflection reflection = new ContractReflection();
        List<Contract> contracts = reflection.getContracts();
        GraphGenerator graphGenerator = new GraphGenerator(contracts);
        graphGenerator.generateGraph();
        graphGenerator.exportAsSVG("pfe.terrain.generatorService.graph.svg");
        System.out.println("Graph generated as pfe.terrain.generatorService.graph.svg");
    }

}
