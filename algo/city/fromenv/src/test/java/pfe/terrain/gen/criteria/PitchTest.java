package pfe.terrain.gen.criteria;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.criteria.Pitch.PITCH_KEY;

public class PitchTest {

    private Pitch pitch;
    private Map<Face, Double> scores;

    private Face lowPitch;
    private Face highPitch;

    @Before
    public void setUp() {
        pitch = new Pitch();
        scores = new HashMap<>();
        lowPitch = generateFace(0, 10);
        highPitch = generateFace(1, 45);
        scores.put(lowPitch, 0.0);
        scores.put(highPitch, 0.0);
    }

    private Face generateFace(double seed, double pitch) {
        Face face = new Face(new Coord(seed, 0), new HashSet<>());
        face.putProperty(PITCH_KEY, new DoubleType(pitch));
        return face;
    }

    @Test
    public void penalizeHighPitch() {
        pitch.assignScores(scores);
        assertThat(scores.get(lowPitch), greaterThan(0.0));
        assertThat(scores.get(highPitch), greaterThan(0.0));
        assertThat(scores.get(lowPitch), greaterThan(scores.get(highPitch)));
    }


}
