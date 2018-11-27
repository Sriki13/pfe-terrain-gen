package pfe.terrain.gen.algo.height;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.SerializableKey;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.*;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static pfe.terrain.gen.algo.constraints.Contract.verticesPrefix;

public class MultiplicationTest {

    private static final Key<BooleanType> vertexWaterKey = new Key<>(verticesPrefix + "IS_WATER", BooleanType.class);
    public static final Key<DoubleType> vertexHeightKey =
            new SerializableKey<>(verticesPrefix + "HEIGHT", "height", DoubleType.class);

    private IslandMap map;
    private CoordSet coords;
    private EdgeSet edges;
    private int mapSize;

    @Before
    public void setUp() throws Exception {
        HeightMultiplication heightGen = new HeightMultiplication();
        map = new IslandMap();
        map.putProperty(new Key<>("SIZE", Integer.class), mapSize);
        map.putProperty(new Key<>("SEED", Integer.class), 3);
        Random random = new Random(map.getSeed());
        coords = new CoordSet();
        edges = new EdgeSet();
        mapSize = 64;
        List<Coord> coordsMatrix = new ArrayList<>(Collections.nCopies(mapSize * mapSize, new Coord(0, 0)));
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                Coord coord = new Coord(i, j);

                coord.putProperty(vertexHeightKey,new DoubleType(5));
            }
        }

        map.putProperty(new Key<>("VERTICES", CoordSet.class), coords);
    }

    @Test
    public void valuesAreOk() throws NoSuchKeyException, KeyTypeMismatch , DuplicateKeyException {
        Map<Coord,Double> heightMap = new HashMap<>();

        coords = map.getVertices();
        for (Coord coord : coords) {
            heightMap.put(coord,coord.getProperty(vertexHeightKey).value);
        }

        HeightMultiplication mult = new HeightMultiplication();

        mult.execute(map,new Context());

        coords = map.getVertices();
        for (Coord coord : coords) {
            Double oldHeight = heightMap.get(coord);

            assertEquals(oldHeight * 2 , coord.getProperty(vertexHeightKey).value);
        }
    }
}
