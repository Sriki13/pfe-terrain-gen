package pfe.terrain.gen.criteria;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.*;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.criteria.HeightLevel.HEIGHT_KEY;

public class HeightLevelTest {

    private HeightLevel heightLevel;
    private Map<Face, Double> scores;

    private Face tooLow;
    private Face perfect;
    private Face okay;
    private Face tooHigh;

    @Before
    public void setUp() {
        tooLow = generateFace(0, 0);
        perfect = generateFace(1, 10 * HeightLevel.RANGE_START);
        okay = generateFace(2, 10 * (HeightLevel.RANGE_START + 0.2));
        tooHigh = generateFace(3, 10);
        List<Face> allFaces = Arrays.asList(
                tooLow, perfect, okay, tooHigh
        );
        this.heightLevel = new HeightLevel(new HashSet<>(allFaces));
        this.scores = new HashMap<>();
        allFaces.forEach(face -> scores.put(face, 0.0));
    }

    private Face generateFace(int seed, double height) {
        Face face = new Face(new Coord(seed, 0), new HashSet<>());
        face.getCenter().putProperty(HEIGHT_KEY, new DoubleType(height));
        return face;
    }

    @Test
    public void assignBonusFromHeight() {
        heightLevel.assignScores(scores);
        assertThat(scores.get(tooHigh), closeTo(0.0, 0.001));
        assertThat(scores.get(tooLow), closeTo(0.0, 0.001));
        assertThat(scores.get(perfect), greaterThan(0.0));
        assertThat(scores.get(okay), greaterThan(0.0));
        assertThat(scores.get(perfect), greaterThan(scores.get(okay)));
    }

}
