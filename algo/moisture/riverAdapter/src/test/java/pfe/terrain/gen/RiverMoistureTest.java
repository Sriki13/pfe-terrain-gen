package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.geometry.*;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.OptionalIntegerType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RiverMoistureTest {

    private IslandMap islandMap;
    private RiverMoisture riverMoisture;

    private Face farFromRiver;
    private Face closeToRiver;
    private Face oneTileAway;

    @Before
    public void setUp() {
        islandMap = new IslandMap();
        riverMoisture = new RiverMoisture();
        EdgeSet allEdges = new EdgeSet(new HashSet<>());
        farFromRiver = generateFace(0, false, allEdges);
        closeToRiver = generateFace(1, true, allEdges);
        oneTileAway = generateFace(2, false, allEdges);
        closeToRiver.addNeighbor(oneTileAway);
        islandMap.putProperty(Contract.faces, new FaceSet(new HashSet<>(Arrays.asList(
                farFromRiver, closeToRiver, oneTileAway
        ))));
        islandMap.putProperty(Contract.edges, allEdges);
    }

    public static Face generateFace(int seed, boolean hasRiver, Set<Edge> allEdges) {
        Edge edge = new Edge(new Coord(seed, 0), new Coord(seed, 1));
        edge.putProperty(AdapterUtils.riverFlowKey, new OptionalIntegerType(hasRiver ? 1 : 0));
        allEdges.add(edge);
        Face result = new Face(new Coord(seed, 2), Collections.singleton(edge));
        result.putProperty(AdapterUtils.faceMoisture, new DoubleType(0.0));
        return result;
    }

    @Test
    public void addMoistureTest() {
        riverMoisture.execute(islandMap, new Context());
        assertThat(getMoisture(farFromRiver), closeTo(0.0, 0.001));
        for (Face face : Arrays.asList(closeToRiver, oneTileAway)) {
            assertThat(getMoisture(face), greaterThan(0.0));
        }
        assertThat(getMoisture(oneTileAway), lessThan(getMoisture(closeToRiver)));
    }

    private double getMoisture(Face face) {
        return face.getProperty(AdapterUtils.faceMoisture).value;
    }


}
