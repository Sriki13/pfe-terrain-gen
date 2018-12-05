package pfe.terrain.gen.water;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AnyOf.anyOf;
import static pfe.terrain.gen.water.NoiseWaterGeneration.*;

public class NoiseWaterGenerationTest {

    private TerrainMap map;
    private FaceSet faces;
    private int mapSize;


    @Before
    public void setUp() throws Exception {
        NoiseWaterGeneration waterGen = new NoiseWaterGeneration();
        map = new TerrainMap();
        faces = new FaceSet();
        mapSize = 400;
        for (float i = 0; i < mapSize; i += 1) {
            for (float j = 0; j < mapSize; j += 1) {
                Face face = new Face(new Coord(i, j), new HashSet<>());
                faces.add(face);
            }
        }
        map.putProperty(new Key<>("FACES", FaceSet.class), faces);
        map.putProperty(new Key<>("SIZE", Integer.class), mapSize);
        map.putProperty(new Key<>("SEED", Integer.class), new Random().nextInt());
        Context context = new Context();
        context.putParam(ARCHIPELAGO_TENDENCY_PARAM, 1.0);
        context.putParam(COAST_ROUGHNESS_PARAM, 0.0);
        waterGen.execute(map, context);
    }

    @Test
    public void testPropertyIsthere() throws NoSuchKeyException, KeyTypeMismatch {
        faces = map.getProperty(FACES);
        for (Face face : faces) {
            assertThat(face.getProperty(NoiseWaterGeneration.FACE_WATER_KEY).value, anyOf(is(true), is(false)));
            if (face.getProperty(NoiseWaterGeneration.FACE_WATER_KEY).value) {
                assertThat(face.getProperty(WATER_KIND_KEY), is(WaterKind.OCEAN));
            } else {
                assertThat(face.getProperty(WATER_KIND_KEY), is(WaterKind.NONE));
            }
        }
    }

    @Test
    @Ignore
    public void printIslandOutline() throws NoSuchKeyException, KeyTypeMismatch {
        faces = map.getProperty(FACES);
        final BufferedImage image = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_USHORT_GRAY);
        short[] data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
        for (Face face : faces) {
            if (face.getProperty(NoiseWaterGeneration.FACE_WATER_KEY).value) {
                data[Math.toIntExact(Math.round(face.getCenter().y)) * mapSize + Math.toIntExact(Math.round(face.getCenter().x))] = (short) 32473;
            } else {
                data[Math.toIntExact(Math.round(face.getCenter().y)) * mapSize + Math.toIntExact(Math.round(face.getCenter().x))] = (short) 65535;
            }
        }
        try {
            ImageIO.write(image, "PNG", new File("islandOutline.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
