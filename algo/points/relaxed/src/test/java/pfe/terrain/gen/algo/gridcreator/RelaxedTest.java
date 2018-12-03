package pfe.terrain.gen.algo.gridcreator;

import org.junit.Test;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.key.Key;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.AllOf.allOf;
import static pfe.terrain.gen.algo.constraints.Contract.seed;
import static pfe.terrain.gen.algo.constraints.Contract.size;

/**
 * Unit test for simple App.
 */
public class RelaxedTest {
    @Test
    public void relaxedTest() throws Exception {

        IslandMap map = new IslandMap();

        int sizeV = 64;
        int nbPointsV = 64;

        map.putProperty(size, sizeV);
        map.putProperty(seed, 0);

        RelaxedPoints builder = new RelaxedPoints();

        Context c = new Context();
        c.putParam(RelaxedPoints.nbPoints, nbPointsV);
        c.putParam(RelaxedPoints.nbIter, 1);

        builder.execute(map, c);

//        System.out.println(map.getProperty(new Key<>("POINTS", CoordSet.class)));
        CoordSet points = (map.getProperty(new Key<>("POINTS", CoordSet.class)));
        assertThat(points, notNullValue());
        assertThat(points.size(), equalTo(64));
        for (Coord point : points) {
            assertThat(point.x, allOf(greaterThanOrEqualTo(0.0), lessThanOrEqualTo((double) sizeV)));
            assertThat(point.y, allOf(greaterThanOrEqualTo(0.0), lessThanOrEqualTo((double) sizeV)));
        }
    }
}