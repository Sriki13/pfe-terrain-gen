package pfe.terrain.gen.algo.height;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.*;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.key.Key;
import pfe.terrain.gen.algo.key.SerializableKey;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static pfe.terrain.gen.algo.constraints.Contract.verticesPrefix;

public class HeightFromWaterTest {

    private static final Key<BooleanType> vertexWaterKey = new Key<>(verticesPrefix + "IS_WATER", BooleanType.class);
    public static final Key<DoubleType> vertexHeightKey =
            new SerializableKey<>(verticesPrefix + "HEIGHT", "height", DoubleType.class);

    private IslandMap map;
    private CoordSet coords;
    private EdgeSet edges;
    private int mapSize;

    @Before
    public void setUp() throws Exception {
        HeightFromWater heightGen = new HeightFromWater();
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
                int lim = 4 + random.nextInt(2);
                if (i < lim || i > mapSize - lim || j < lim || j > mapSize - lim) {
                    coord.putProperty(vertexWaterKey, new BooleanType(true));
                } else {
                    coord.putProperty(vertexWaterKey, new BooleanType(false));
                }
                coords.add(coord);
                coordsMatrix.set(j * mapSize + i, coord);
            }
        }
        for (int i = 1; i < mapSize - 1; i += 1) {
            for (int j = 1; j < mapSize - 1; j += 1) {
                edges.add(new Edge(coordsMatrix.get(j * mapSize + i), coordsMatrix.get(j * mapSize + i + 1)));
                edges.add(new Edge(coordsMatrix.get(j * mapSize + i), coordsMatrix.get((j + 1) * mapSize + i)));
            }
        }
        FaceSet faces = new FaceSet();
        map.putProperty(new Key<>("VERTICES", CoordSet.class), coords);
        map.putProperty(new Key<>("EDGES", EdgeSet.class), edges);
        map.putProperty(new Key<>("FACES", FaceSet.class), faces);
        heightGen.execute(map, new Context());
    }

    @Test
    public void valuesAreOk() throws NoSuchKeyException, KeyTypeMismatch {
        coords = map.getVertices();
        for (Coord coord : coords) {
            assertThat(coord.getProperty(vertexHeightKey).value, is(greaterThanOrEqualTo(0.0)));
            if (coord.getProperty(vertexHeightKey).value < 0.0) {
                assertThat(coord.getProperty(vertexWaterKey).value, is(true));
            } else if (coord.getProperty(vertexHeightKey).value > 5.0) {
                assertThat(coord.getProperty(vertexWaterKey).value, is(false));
            }
        }
    }

    @Ignore
    @Test
    public void testVisualizeFinal() throws NoSuchKeyException, KeyTypeMismatch {
        coords = map.getVertices();
        final BufferedImage image = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_USHORT_GRAY);
        short[] data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
        for (Coord coord : coords) {
            double height = coord.getProperty(vertexHeightKey).value;
            data[Math.toIntExact(Math.round(coord.y * mapSize + coord.x))] = (short) (height * 1000);
        }
        try {
            ImageIO.write(image, "PNG", new File("heightmap.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
