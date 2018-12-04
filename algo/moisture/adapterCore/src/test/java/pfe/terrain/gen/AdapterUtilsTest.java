package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.IntegerType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AdapterUtilsTest {

    private AdapterUtils utils;

    @Before
    public void setUp() {
        utils = new AdapterUtils();
    }

    private Face generateFace(int seed, double moisture) {
        return generateFace(seed, moisture, new HashSet<>());
    }

    private Face generateFace(int seed, boolean isLake) {
        Face face = generateFace(seed, 1);
        face.putProperty(AdapterUtils.FACE_WATER_KEY, new BooleanType(isLake));
        face.putProperty(AdapterUtils.WATER_KIND_KEY, isLake ? WaterKind.LAKE : WaterKind.NONE);
        return face;
    }

    private Face generateFace(int seed, double moisture, Set<Edge> edges) {
        Face face = new Face(new Coord(seed, 0), edges);
        face.putProperty(AdapterUtils.FACE_MOISTURE, new DoubleType(moisture));
        return face;
    }

    private double getMoisture(Face face) {
        return face.getProperty(AdapterUtils.FACE_MOISTURE).value;
    }

    @Test
    public void addMoistureTest() {
        Face face = generateFace(0, 0.1);
        utils.addMoisture(face, 0.1);
        assertThat(getMoisture(face), closeTo(0.2, 0.001));
    }

    @Test
    public void dontGoOverMoistureLimit() {
        Face face = generateFace(0, 0.5);
        utils.addMoisture(face, AdapterUtils.MOISTURE_LIMIT);
        assertThat(getMoisture(face), closeTo(1, 0.001));
    }

    @Test
    public void spreadToNeighborsTest() {
        Face face = generateFace(0, 0.5);
        Face neighbor = generateFace(1, 0.2);
        Face alreadyDone = generateFace(2, 0.2);
        face.addNeighbor(neighbor);
        face.addNeighbor(alreadyDone);
        Set<Face> seen = new HashSet<>(Collections.singleton(alreadyDone));
        utils.spreadToNeighbours(face, seen, 0.2);
        assertThat(getMoisture(face), closeTo(0.5, 0.001));
        assertThat(getMoisture(neighbor), closeTo(0.4, 0.001));
        assertThat(getMoisture(alreadyDone), closeTo(0.2, 0.001));
        assertThat(seen.size(), is(2));
    }

    @Test
    public void tilesNextToRiverTest() {
        Edge river = new Edge(new Coord(0, 1), new Coord(1, 0));
        river.putProperty(AdapterUtils.RIVER_FLOW_KEY, new IntegerType(1));
        Face correct = generateFace(2, 0.5, new HashSet<>(Collections.singleton(river)));
        Face incorrect = generateFace(3, 0.5);
        Set<Face> got = utils.getTilesNextToRivers(new HashSet<>(Arrays.asList(correct, incorrect)));
        assertThat(got.size(), is(1));
        assertThat(got.contains(correct), is(true));
    }

    @Test
    public void tilesNextToLakesTest() {
        Face correct = generateFace(0, false);
        Face lake = generateFace(1, true);
        correct.addNeighbor(lake);
        lake.addNeighbor(correct);
        Face incorrect = generateFace(2, false);
        Set<Face> got = utils.getTilesNextToLakes(new HashSet<>(Arrays.asList(correct, incorrect, lake)));
        assertThat(got.size(), is(1));
        assertThat(got.contains(correct), is(true));
    }

}
