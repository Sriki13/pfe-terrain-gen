package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.criteria.Criterion;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pfe.terrain.gen.CityContract.CITY_KEY;
import static pfe.terrain.gen.algo.constraints.Contract.FACES;
import static pfe.terrain.gen.criteria.HeightLevel.HEIGHT_KEY;

public class CityGeneratorTest {

    private static final Coord center = new Coord(0, 0);

    private Criterion distanceCenterCriterion =
            (context, scores) -> scores.forEach((key, value) -> scores.put(key, value + 1 / center.distance(key.getCenter())));

    private Criterion heightCriterion =
            (context, scores) -> scores.forEach((key, value) -> scores.put(key, value + key.getCenter().getProperty(HEIGHT_KEY).value));

    private CityGenerator cityGenerator;

    private Face bestCandidate;
    private Face secondBest;
    private Face thirdBest;

    @Before
    public void setUp() {
        cityGenerator = new CityGenerator(Arrays.asList(distanceCenterCriterion, heightCriterion));
        bestCandidate = generateFace(1, 10);
        secondBest = generateFace(3, 10);
        thirdBest = generateFace(2, 5);
        IslandMap islandMap = new IslandMap();
        islandMap.putProperty(FACES, new FaceSet(new HashSet<>(Arrays.asList(
                secondBest, thirdBest, bestCandidate
        ))));
    }

    private Face generateFace(double x, double z) {
        Face face = new Face(new Coord(x, (double) 1), new HashSet<>());
        face.getCenter().putProperty(HEIGHT_KEY, new DoubleType(z));
        return face;
    }

    @Test
    public void placeCitiesCorrectly() {
        Context context = new Context();
        context.putParam(CityGenerator.NB_CITIES, 2);
        cityGenerator.generateCities(context, new HashSet<>(Arrays.asList(
                secondBest, thirdBest, bestCandidate
        )));
        assertTrue(bestCandidate.hasProperty(CITY_KEY));
        assertTrue(secondBest.hasProperty(CITY_KEY));
        assertFalse(thirdBest.hasProperty(CITY_KEY));
    }

}
