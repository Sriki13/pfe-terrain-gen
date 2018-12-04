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
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.algo.height.HeightMultiplication.VERTEX_HEIGHT_KEY;

public class MultiplicationTest {

    private IslandMap map;
    private CoordSet coords;
    private int mapSize;

    @Before
    public void setUp() {
        map = new IslandMap();
        map.putProperty(new Key<>("SIZE", Integer.class), mapSize);
        map.putProperty(new Key<>("SEED", Integer.class), 3);
        coords = new CoordSet();
        mapSize = 64;
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                Coord coord = new Coord(i, j);

                coord.putProperty(VERTEX_HEIGHT_KEY, new DoubleType(5));
            }
        }

        map.putProperty(new Key<>("VERTICES", CoordSet.class), coords);
    }

    @Test
    public void valuesAreOk() throws NoSuchKeyException, KeyTypeMismatch , DuplicateKeyException {
        Map<Coord,Double> heightMap = new HashMap<>();

        coords = map.getVertices();
        for (Coord coord : coords) {
            heightMap.put(coord, coord.getProperty(VERTEX_HEIGHT_KEY).value);
        }

        HeightMultiplication multiplier = new HeightMultiplication();

        multiplier.execute(map, new Context());

        coords = map.getVertices();
        for (Coord coord : coords) {
            Double oldHeight = heightMap.get(coord);

            assertThat(oldHeight * 2, closeTo(coord.getProperty(VERTEX_HEIGHT_KEY).value, 0.01));
        }
    }
}
