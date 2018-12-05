package pfe.terrain.gen.algo.gridcreator;

import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.CoordSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.AllOf.allOf;
import static pfe.terrain.gen.algo.constraints.Contract.SEED;
import static pfe.terrain.gen.algo.constraints.Contract.SIZE;

/**
 * Unit test for simple App.
 */
public class RelaxedTest {
    @Test
    public void relaxedTest() throws Exception {

        TerrainMap map = new TerrainMap();

        int sizeV = 64;
        int nbPointsV = 64;

        map.putProperty(SIZE, sizeV);
        map.putProperty(SEED, 0);

        RelaxedPoints builder = new RelaxedPoints();

        Context c = new Context();
        c.putParam(RelaxedPoints.NB_POINTS, nbPointsV);
        c.putParam(RelaxedPoints.NB_ITER, 1);

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
