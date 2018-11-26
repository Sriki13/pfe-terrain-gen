package pfe.terrain.generatorService.graph;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import pfe.terrain.gen.FinalContract;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Contract;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static guru.nidi.graphviz.model.Factory.mutNode;

public class GraphGenerator {

    private List<Contract> contracts;

    private MutableGraph graph;
    private Map<String, MutableNode> properties;

    public GraphGenerator(List<Contract> contracts) {
        this.contracts = contracts;
        this.properties = new HashMap<>();
    }

    public void generateGraph() {
        this.graph = Factory.mutGraph().setDirected(true);
        for (Contract contract : contracts) {
            processProperties(contract.getContract().getRequired());
            processProperties(contract.getContract().getCreated());
        }
        properties.values().forEach(node -> graph.add(node));
        contracts.forEach(this::addAndLinkContract);
    }

    private void processProperties(Set<Key> keys) {
        for (Key key : keys) {
            if (properties.containsKey(key.getId())) {
                continue;
            }
            String name = key.getId() + "\n" + key.getType().getSimpleName();
            if (key.isSerialized()) {
                name += "\n(serialized as " + key.getSerializedName() + ")";
            }
            properties.put(key.getId(), mutNode(name).add(Color.BLACK).add(Shape.RECTANGLE));
        }
    }

    private void addAndLinkContract(Contract contract) {
        if (contract.getName().equals(FinalContract.FINAL_CONTRACT_NAME)) {
            return;
        }
        MutableNode contractNode = mutNode(contract.getName())
                .add(Color.RED).add(Shape.ELLIPSE);
        graph.add(contractNode);
        contract.getContract().getCreated().forEach(key ->
                contractNode.addLink(properties.get(key.getId())).add(Style.BOLD));
        contract.getContract().getRequired().forEach(key ->
                properties.get(key.getId()).addLink(contractNode));
    }

    public String exportAsJSON() {
        return export(Format.JSON);
    }

    public String exportAsXDot() {
        return export(Format.XDOT);
    }

    private String export(Format format) {
        return Graphviz.fromGraph(graph).render(format).toString();
    }

    public void exportAsPNG(String filename) throws IOException {
        Graphviz.fromGraph(graph).width(900).render(Format.PNG).toFile(new File(filename));
    }

}
