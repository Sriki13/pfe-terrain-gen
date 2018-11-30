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
import java.util.*;

import static guru.nidi.graphviz.model.Factory.mutNode;

public class GraphGenerator {

    private List<Contract> contracts;

    private MutableGraph graph;
    private Map<String, MutableNode> properties;
    private Map<String, String> keyToName;
    private List<String> serialized;

    public GraphGenerator(List<Contract> contracts) {
        this.contracts = contracts;
        this.properties = new HashMap<>();
        this.keyToName = new HashMap<>();
        this.serialized = new ArrayList<>();
    }

    public void generateGraph() {
        this.graph = Factory.mutGraph().setDirected(true);
        for (Contract contract : contracts) {
            processProperties(contract.getContract().getRequired());
            processProperties(contract.getContract().getCreated());
            processProperties(contract.getContract().getModified());
        }
        for (Map.Entry<String, String> entry : keyToName.entrySet()) {
            boolean isSerialized = serialized.contains(entry.getKey());
            MutableNode node = mutNode(entry.getValue())
                    .add(isSerialized ? Color.GREEN : Color.BLACK)
                    .add(Shape.RECTANGLE);
            if (isSerialized) {
                node.add(Style.BOLD);
            }
            properties.put(entry.getKey(), node);
        }
        properties.values().forEach(node -> graph.add(node));
        contracts.forEach(this::addAndLinkContract);
    }

    private void processProperties(Set<Key> keys) {
        for (Key key : keys) {
            if (!keyToName.containsKey(key.getId()) ||
                    (key.isSerialized() && !keyToName.get(key.getId()).contains("serialized"))) {
                String name = key.getId() + "\n" + key.getType().getSimpleName();
                if (key.isSerialized()) {
                    name += "\n(serialized as " + key.getSerializedName() + ")";
                    serialized.add(key.getId());
                }
                keyToName.put(key.getId(), name);
            }
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
        contract.getContract().getModified().forEach(key -> {
            contractNode.addLink(properties.get(key.getId())).add(Style.BOLD);
            properties.get(key.getId()).addLink(contractNode);
        });
    }

    public String exportAsXDot() {
        return export(Format.XDOT);
    }

    public String exportAsSVG() {
        return export(Format.SVG);
    }

    private String export(Format format) {
        return Graphviz.fromGraph(graph).render(format).toString();
    }

    public void exportAsSVG(String filename) throws IOException {
        Graphviz.fromGraph(graph).width(1500).render(Format.SVG).toFile(new File(filename));
    }

}
