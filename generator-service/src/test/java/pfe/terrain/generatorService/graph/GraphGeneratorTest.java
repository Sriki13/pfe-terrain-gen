package pfe.terrain.generatorService.graph;

import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import guru.nidi.graphviz.parse.Parser;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.types.IntegerType;
import pfe.terrain.generatorService.TestContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class GraphGeneratorTest {

    private GraphGenerator graphGenerator;

    @Before
    public void setUp() {
        graphGenerator = new GraphGenerator(Arrays.asList(
                new TestContract("init", Arrays.asList(
                        new Key<>("SIZE", IntegerType.class),
                        new Key<>("SEED", IntegerType.class)),
                        new ArrayList<>()
                ),
                new TestContract("pointsGen", Collections.singletonList(
                        new Key<>("POINTS", IntegerType.class)),
                        Arrays.asList(
                                new Key<>("SIZE", IntegerType.class),
                                new Key<>("SEED", IntegerType.class)
                        )
                ),
                new TestContract("mesh",
                        Arrays.asList(
                                new Key<>("VERTICES", IntegerType.class),
                                new Key<>("EDGES", IntegerType.class),
                                new Key<>("FACES", IntegerType.class)
                        ),
                        Arrays.asList(
                                new Key<>("SIZE", IntegerType.class),
                                new Key<>("SEED", IntegerType.class),
                                new Key<>("POINTS", IntegerType.class))
                ),
                new TestContract("border", Collections.singletonList(
                        new Key<>("BORDER", IntegerType.class)),
                        Arrays.asList(
                                new Key<>("VERTICES", IntegerType.class),
                                new Key<>("EDGES", IntegerType.class),
                                new Key<>("FACES", IntegerType.class)
                        )
                ),
                new TestContract("modified", new ArrayList<>(), new ArrayList<>(),
                        Collections.singletonList(new Key<>("BORDER", IntegerType.class)))
        ));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void graphTest() throws Exception {
        graphGenerator.generateGraph();
        //graphGenerator.exportAsSVG("test.svg");
        MutableGraph graph = Parser.read(graphGenerator.exportAsXDot());
        assertThat(graph.nodes().size(), is(12));
        List<MutableNode> allNodes = new ArrayList<>(graph.nodes());
        Arrays.asList("init", "points", "mesh", "border", "modified").forEach(str ->
                assertTrue(isInNodes(allNodes, str)));
        Arrays.asList("SIZE", "SEED", "POINTS", "VERTICES", "EDGES", "FACES", "BORDER").forEach(str ->
                assertTrue(isInNodes(allNodes, str)));
        MutableNode init = findNode(allNodes, "init");
        assertThat(init.links().size(), is(2));
        assertTrue(nodeIsLinked(init, "SIZE"));
        assertTrue(nodeIsLinked(init, "SEED"));
        MutableNode seed = findNode(allNodes, "SEED");
        assertThat(seed.links().size(), is(2));
        assertTrue(nodeIsLinked(seed, "points"));
        assertTrue(nodeIsLinked(seed, "mesh"));
        MutableNode mod = findNode(allNodes, "modified");
        assertThat(mod.links().size(), is(1));
        assertTrue(nodeIsLinked(mod, "BORDER"));
        MutableNode border = findNode(allNodes, "BORDER");
        assertTrue(nodeIsLinked(border, "modified"));
    }

    private boolean isInNodes(List<MutableNode> nodes, String labelStart) {
        return findNode(nodes, labelStart) != null;
    }

    private MutableNode findNode(List<MutableNode> nodes, String labelStart) {
        for (MutableNode node : nodes) {
            if (node.name().toString().startsWith(labelStart)) {
                return node;
            }
        }
        return null;
    }

    private boolean nodeIsLinked(MutableNode start, String endLabelStart) {
        for (Link link : start.links()) {
            if (link.to().toString().startsWith(endLabelStart)) {
                return true;
            }
        }
        return false;
    }

}
