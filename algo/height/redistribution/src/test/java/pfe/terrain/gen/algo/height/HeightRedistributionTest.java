package pfe.terrain.gen.algo.height;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static pfe.terrain.gen.algo.constraints.Contract.FACES;
import static pfe.terrain.gen.algo.constraints.Contract.VERTICES;
import static pfe.terrain.gen.algo.height.HeightRedistribution.VERTEX_HEIGHT_KEY;
import static pfe.terrain.gen.algo.height.HeightRedistribution.VERTEX_WATER_KEY;

public class HeightRedistributionTest {

    private IslandMap map;
    private int mapSize;

    @Before
    public void setUp() throws Exception {
        map = new IslandMap();
        map.putProperty(new Key<>("SIZE", Integer.class), mapSize);
        map.putProperty(new Key<>("SEED", Integer.class), 3);
        CoordSet coords = new CoordSet();
        mapSize = 64;
        int halfSize = mapSize / 2;
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                Coord coord = new Coord(i, j);
                if (i < 1 || i > mapSize || j < 1 || j > mapSize) {
                    coord.putProperty(VERTEX_WATER_KEY, new BooleanType(true));
                    coord.putProperty(VERTEX_HEIGHT_KEY, new DoubleType(0));
                } else {
                    coord.putProperty(VERTEX_WATER_KEY, new BooleanType(false));
                    if (i < halfSize) {
                        if (j < halfSize) {
                            coord.putProperty(VERTEX_HEIGHT_KEY, new DoubleType(i + j));
                        } else {
                            coord.putProperty(VERTEX_HEIGHT_KEY, new DoubleType(i + (j - (j - halfSize))));
                        }
                    } else {
                        if (j < halfSize) {
                            coord.putProperty(VERTEX_HEIGHT_KEY, new DoubleType((i - (i - halfSize) + j)));
                        } else {
                            coord.putProperty(VERTEX_HEIGHT_KEY, new DoubleType((i - (i - halfSize) + (j - (j - halfSize)))));
                        }
                    }
                }
                coords.add(coord);
            }
        }
        map.putProperty(FACES, new FaceSet());
        map.putProperty(VERTICES, coords);
    }

    @Test
    public void checkRepartition() throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        HeightRedistribution heightGen = new HeightRedistribution();
        List<Coord> coordList = new ArrayList<>(map.getVertices());
        coordList.sort(compare());
        double median = coordList.get(coordList.size() / 2).getProperty(VERTEX_HEIGHT_KEY).value;
        heightGen.execute(map, new Context());
        coordList = new ArrayList<>(map.getVertices());
        coordList.sort(compare());
        double medianAfter = coordList.get(coordList.size() / 2).getProperty(VERTEX_HEIGHT_KEY).value;

        // Assert that there are more low values now
        assertThat(medianAfter, lessThan(median));
    }

    private Comparator<Coord> compare() {
        return (o1, o2) -> {
            try {
                return (int) (1000 * o2.getProperty(VERTEX_HEIGHT_KEY).value - o1.getProperty(VERTEX_HEIGHT_KEY).value);
            } catch (NoSuchKeyException | KeyTypeMismatch e) {
                e.printStackTrace();
            }
            return 0;
        };
    }
}
