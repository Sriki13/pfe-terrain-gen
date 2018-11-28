package pfe.terrain.gen.algo.height;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.EdgeSet;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import static pfe.terrain.gen.algo.height.HeightRedistribution.vertexHeightKey;
import static pfe.terrain.gen.algo.height.HeightRedistribution.vertexWaterKey;

public class HeightRedistributionTest {

    private IslandMap map;
    private CoordSet coords;
    private EdgeSet edges;
    private int mapSize;

    @Before
    public void setUp() throws Exception {
        HeightRedistribution heightGen = new HeightRedistribution();
        map = new IslandMap();
        map.putProperty(new Key<>("SIZE", Integer.class), mapSize);
        map.putProperty(new Key<>("SEED", Integer.class), 3);
        coords = new CoordSet();
        edges = new EdgeSet();
        mapSize = 64;
        int halfSize = mapSize / 2;
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                Coord coord = new Coord(i, j);
                if (i < 1 || i > mapSize || j < 1 || j > mapSize) {
                    coord.putProperty(vertexWaterKey, new BooleanType(true));
                    coord.putProperty(vertexHeightKey, new DoubleType(0));
                } else {
                    coord.putProperty(vertexWaterKey, new BooleanType(false));
                    if (i < halfSize) {
                        if (j < halfSize) {
                            coord.putProperty(vertexHeightKey, new DoubleType(i + j));
                        } else {
                            coord.putProperty(vertexHeightKey, new DoubleType(i + (j - (j - halfSize))));
                        }
                    } else {
                        if (j < halfSize) {
                            coord.putProperty(vertexHeightKey, new DoubleType((i - (i - halfSize) + j)));
                        } else {
                            coord.putProperty(vertexHeightKey, new DoubleType((i - (i - halfSize) + (j - (j - halfSize)))));
                        }
                    }
                }
                coords.add(coord);
            }
        }
        map.putProperty(new Key<>("VERTICES", CoordSet.class), coords);
    }

    @Test
    public void valuesAreOk() throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {

    }
}
