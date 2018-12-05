package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.*;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.IntegerType;
import pfe.terrain.gen.algo.types.OptionalIntegerType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RiverLakeMoistureTest {

    private TerrainMap terrainMap;
    private RiverLakeMoisture riverLakeMoisture;

    private Face farFromRiver;
    private Face closeToRiver;
    private Face oneTileFromRiver;

    private Face farFromLake;
    private Face closeToLake;
    private Face oneTileFromLake;

    private Face closeToRiverAndLake;

    @Before
    public void setUp() {
        terrainMap = new TerrainMap();
        riverLakeMoisture = new RiverLakeMoisture();
        EdgeSet allEdges = new EdgeSet(new HashSet<>());
        farFromRiver = generateFace(0, false, allEdges);
        closeToRiver = generateFace(1, true, allEdges);
        oneTileFromRiver = generateFace(2, false, allEdges);
        closeToRiver.addNeighbor(oneTileFromRiver);

        farFromLake = generateFace(3, false);
        closeToLake = generateFace(4, false);
        oneTileFromLake = generateFace(5, false);
        Face lake = generateFace(6, true);
        closeToLake.addNeighbor(oneTileFromLake);
        oneTileFromLake.addNeighbor(closeToLake);
        lake.addNeighbor(closeToLake);
        closeToLake.addNeighbor(lake);

        closeToRiverAndLake = generateFace(7, false);
        closeToRiverAndLake.getEdges().forEach(edge ->
                edge.putProperty(AdapterUtils.RIVER_FLOW_KEY, new IntegerType(1)));
        lake.addNeighbor(closeToRiverAndLake);
        closeToRiverAndLake.addNeighbor(lake);

        terrainMap.putProperty(Contract.FACES, new FaceSet(new HashSet<>(Arrays.asList(
                farFromRiver, closeToRiver, oneTileFromRiver, farFromLake, closeToLake, oneTileFromLake, lake
        ))));
        terrainMap.putProperty(Contract.EDGES, allEdges);
    }

    public static Face generateFace(int seed, boolean hasRiver, Set<Edge> allEdges) {
        Edge edge = new Edge(new Coord(seed, 0), new Coord(seed, 1));
        if (hasRiver) {
            edge.putProperty(AdapterUtils.RIVER_FLOW_KEY, new OptionalIntegerType(1));
        }
        allEdges.add(edge);
        Face result = new Face(new Coord(seed, 2), Collections.singleton(edge));
        result.putProperty(AdapterUtils.FACE_MOISTURE, new DoubleType(0.0));
        result.putProperty(AdapterUtils.FACE_MOISTURE, new DoubleType(0.0));
        result.putProperty(AdapterUtils.FACE_WATER_KEY, new BooleanType(false));
        result.putProperty(AdapterUtils.WATER_KIND_KEY, WaterKind.NONE);
        return result;
    }

    @SuppressWarnings("Duplicates") // no test lib necessary for a single test util method
    public static Face generateFace(int seed, boolean isLake) {
        Face result = new Face(new Coord(seed, 2), new HashSet<>());
        result.putProperty(AdapterUtils.FACE_MOISTURE, new DoubleType(0.0));
        result.putProperty(AdapterUtils.FACE_WATER_KEY, new BooleanType(isLake));
        result.putProperty(AdapterUtils.WATER_KIND_KEY, isLake ? WaterKind.LAKE : WaterKind.NONE);
        return result;
    }

    @Test
    public void addMoistureTest() {
        Context context = new Context();
        context.putParam(RiverLakeMoisture.moistureParam, 0.2);
        riverLakeMoisture.execute(terrainMap, new Context());
        assertThat(getMoisture(farFromRiver), closeTo(0.0, 0.001));
        for (Face face : Arrays.asList(closeToRiver, oneTileFromRiver)) {
            assertThat(getMoisture(face), greaterThan(0.0));
        }
        assertThat(getMoisture(oneTileFromRiver), lessThan(getMoisture(closeToRiver)));

        assertThat(getMoisture(farFromLake), closeTo(0.0, 0.001));
        for (Face face : Arrays.asList(closeToLake, oneTileFromLake)) {
            assertThat(getMoisture(face), greaterThan(0.0));
        }
        assertThat(getMoisture(oneTileFromLake), lessThan(getMoisture(closeToLake)));

        assertThat(getMoisture(closeToRiverAndLake), closeTo(0.3, 0.001));
    }

    private double getMoisture(Face face) {
        return face.getProperty(AdapterUtils.FACE_MOISTURE).value;
    }

}
