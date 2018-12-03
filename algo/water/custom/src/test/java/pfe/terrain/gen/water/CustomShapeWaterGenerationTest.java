package pfe.terrain.gen.water;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.key.Key;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AnyOf.anyOf;

public class CustomShapeWaterGenerationTest {

    private IslandMap map;
    private FaceSet faces;
    private int mapSize;

    @Before
    public void setUp() throws Exception {
        CustomShapeWaterGeneration waterGen = new CustomShapeWaterGeneration();
        map = new IslandMap();
        faces = new FaceSet();
        mapSize = 256;
        for (float i = 0; i < mapSize; i += 1) {
            for (float j = 0; j < mapSize; j += 1) {
                Face face = new Face(new Coord(i, j), new HashSet<>());
                faces.add(face);
            }
        }
        map.putProperty(new Key<>("FACES", FaceSet.class), faces);
        map.putProperty(new Key<>("SIZE", Integer.class), mapSize);
        map.putProperty(new Key<>("SEED", Integer.class), 347);
        Context context = new Context();
        context.putParam(CustomShapeWaterGeneration.premadeShape, DefaultShape.THONK.name());
        waterGen.execute(map, context);
    }

    @Test
    public void testPropertyIsthere() throws NoSuchKeyException, KeyTypeMismatch {
        faces = map.getFaces();
        for (Face face : faces) {
            assertThat(face.getProperty(CustomShapeWaterGeneration.faceWaterKey).value, anyOf(is(true), is(false)));
            if (face.getProperty(CustomShapeWaterGeneration.faceWaterKey).value) {
                assertThat(face.getProperty(CustomShapeWaterGeneration.waterKindKey), is(WaterKind.OCEAN));
            } else {
                assertThat(face.getProperty(CustomShapeWaterGeneration.waterKindKey), is(WaterKind.NONE));
            }
        }
    }

    @Test
    @Ignore
    public void printIslandOutline() throws NoSuchKeyException, KeyTypeMismatch {
        faces = map.getFaces();
        final BufferedImage image = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_USHORT_GRAY);
        short[] data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
        for (Face face : faces) {
            if (face.getProperty(CustomShapeWaterGeneration.faceWaterKey).value) {
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