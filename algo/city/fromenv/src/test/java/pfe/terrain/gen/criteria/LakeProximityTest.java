package pfe.terrain.gen.criteria;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.island.WaterKind;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.CityGenerator.WATER_KIND_KEY;

public class LakeProximityTest {

    private LakeProximity lakeProximity;
    private Map<Face, Double> scores;

    private Face closeFromLake;
    private Face farFromLake;

    @Before
    public void setUp() {
        Face lake = generateFace(true, 0, 0);
        closeFromLake = generateFace(false, 1, 0);
        farFromLake = generateFace(false, 10, 0);
        lakeProximity = new LakeProximity(new HashSet<>(Collections.singleton(lake)));
        scores = new HashMap<>();
        scores.put(closeFromLake, 0.0);
        scores.put(farFromLake, 0.0);
    }

    private Face generateFace(boolean isLake, double x, double y) {
        Face face = new Face(new Coord(x, y), new HashSet<>());
        face.putProperty(WATER_KIND_KEY, isLake ? WaterKind.LAKE : WaterKind.NONE);
        return face;
    }

    @Test
    public void addBonusIfCloseToLake() {
        lakeProximity.assignScores(scores);
        scores.forEach((key, value) -> assertThat(value, greaterThan(0.0)));
        assertThat(scores.get(closeFromLake), greaterThan(scores.get(farFromLake)));
    }

}
