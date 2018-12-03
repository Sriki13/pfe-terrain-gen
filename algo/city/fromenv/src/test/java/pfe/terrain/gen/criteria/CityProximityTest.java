package pfe.terrain.gen.criteria;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.CityGenerator.CITY_KEY;

public class CityProximityTest {

    private CityProximity cityProximity;
    private Map<Face, Double> scores;

    private Face closeToCity;
    private Face farFromCity;
    private Face city;

    @Before
    public void setUp() {
        cityProximity = new CityProximity();
        scores = new HashMap<>();
        city = generateFace(true, 0, 0);
        closeToCity = generateFace(false, 1, 0);
        farFromCity = generateFace(false, 10, 0);
        scores.put(closeToCity, 0.0);
        scores.put(farFromCity, 0.0);
        scores.put(city, 0.0);
    }

    private Face generateFace(boolean isCity, int x, int y) {
        Face result = new Face(new Coord(x, y), new HashSet<>());
        result.putProperty(CITY_KEY, new BooleanType(isCity));
        return result;
    }

    @Test
    public void noInfluenceWithoutCities() {
        cityProximity.assignScores(scores);
        scores.forEach((key, value) -> assertThat(value, closeTo(0, 0.001)));
    }

    @Test
    public void penalizeIfCloseToCity() {
        cityProximity.addCity(city);
        cityProximity.assignScores(scores);
        assertThat(scores.get(closeToCity), lessThan(scores.get(farFromCity)));
    }

}
