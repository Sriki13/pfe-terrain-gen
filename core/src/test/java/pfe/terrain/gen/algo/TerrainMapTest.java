package pfe.terrain.gen.algo;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.CoordSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class TerrainMapTest {
    private TerrainMap map;

    @Before
    public void init() {
        map = new TerrainMap();
    }

    @Test
    public void test1() throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        map.putProperty(new Key<>("POINTS", CoordSet.class), new CoordSet());
        CoordSet coordinates = map.getProperty(new Key<>("POINTS", CoordSet.class));
        coordinates.add(new Coord(0,0));
        map.putProperty(new Key<>("POINTS", CoordSet.class), coordinates);
        assertThat(map.getProperty(new Key<>("POINTS", CoordSet.class)).size(), equalTo(1));
    }

    @Test(expected = KeyTypeMismatch.class)
    public void test2() throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        map.putProperty(new Key<>("POINTS", CoordSet.class), new CoordSet());
        String coordinates = map.getProperty(new Key<>("POINTS", String.class));
        assertThat(coordinates, equalTo(null));
    }
}
