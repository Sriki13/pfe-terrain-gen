package pfe.terrain.gen.criteria;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.criteria.MoistureLevel.MOISTURE_KEY;

public class MoistureLevelTest {

    private MoistureLevel moistureLevel;
    private Map<Face, Double> scores;

    private Face ideal;
    private Face closeToIdeal;
    private Face farFromIdeal;

    @Before
    public void setUp() {
        moistureLevel = new MoistureLevel();
        scores = new HashMap<>();
        ideal = generateFace(0, MoistureLevel.IDEAL);
        closeToIdeal = generateFace(1, MoistureLevel.IDEAL + 0.1);
        farFromIdeal = generateFace(2, MoistureLevel.IDEAL - 0.4);
        Arrays.asList(ideal, closeToIdeal, farFromIdeal)
                .forEach(face -> scores.put(face, 0.0));
    }

    private Face generateFace(int seed, double moisture) {
        Face face = new Face(new Coord(seed, 0), new HashSet<>());
        face.putProperty(MOISTURE_KEY, new DoubleType(moisture));
        return face;
    }

    @Test
    public void bonusIfMoistureCloseToIdeal() {
        moistureLevel.assignScores(new Context(), scores);
        scores.forEach((key, value) -> assertThat(value, greaterThan(0.0)));
        assertThat(scores.get(ideal), greaterThan(scores.get(closeToIdeal)));
        assertThat(scores.get(closeToIdeal), greaterThan(scores.get(farFromIdeal)));
    }

}
