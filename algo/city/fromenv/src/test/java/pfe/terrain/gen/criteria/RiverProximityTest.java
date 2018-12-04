package pfe.terrain.gen.criteria;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.IntegerType;

import java.util.*;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.criteria.RiverProximity.RIVER_FLOW_KEY;

public class RiverProximityTest {

    private RiverProximity riverProximity;
    private Map<Face, Double> scores;

    private Face closeFromRiver;
    private Face farFromRiver;

    @Before
    public void setUp() {
        Edge river = new Edge(new Coord(0, 0), new Coord(0, 1));
        river.putProperty(RIVER_FLOW_KEY, new IntegerType(1));
        Set<Edge> edges = new HashSet<>(Collections.singleton(river));
        riverProximity = new RiverProximity(edges);
        closeFromRiver = new Face(new Coord(0, 5), new HashSet<>());
        farFromRiver = new Face(new Coord(50, 5), new HashSet<>());
        scores = new HashMap<>();
        scores.put(closeFromRiver, 0.0);
        scores.put(farFromRiver, 0.0);
    }

    @Test
    public void bonusFromRivers() {
        riverProximity.assignScores(scores);
        assertThat(scores.get(closeFromRiver), greaterThan(0.0));
        assertThat(scores.get(farFromRiver), greaterThan(0.0));
        assertThat(scores.get(closeFromRiver), greaterThan(scores.get(farFromRiver)));
    }


}
