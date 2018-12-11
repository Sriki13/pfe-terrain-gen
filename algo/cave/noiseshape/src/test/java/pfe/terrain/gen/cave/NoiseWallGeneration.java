package pfe.terrain.gen.cave;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.TerrainMap;
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
import static pfe.terrain.gen.algo.constraints.Contract.FACES;

public class NoiseWallGeneration {

    private TerrainMap map;
    private FaceSet faces;
    private int mapSize;


    @Before
    public void setUp() throws Exception {
        NoiseWall wallGen = new NoiseWall();
        map = new TerrainMap();
        faces = new FaceSet();
        mapSize = 255;
        for (int i = 0; i < mapSize; i ++) {
            for (int j = 0; j < mapSize; j ++) {
                Face face = new Face(new Coord(i, j), new HashSet<>());
                faces.add(face);
            }
        }
        map.putProperty(new Key<>("FACES", FaceSet.class), faces);
        map.putProperty(new Key<>("SIZE", Integer.class), mapSize);
        map.putProperty(new Key<>("SEED", Integer.class), new Random().nextInt());
        Context context = new Context();
        context.putParam(NoiseWall.MULTIPLE_CAVES_TENDENCY,1.0);
        context.putParam(NoiseWall.CAVE_ROUGHNESS,1.0);
        context.putParam(NoiseWall.NOISE_PARAM,"ridged");
        wallGen.execute(map, context);
    }

    @Test
    public void testPropertyIsthere() throws NoSuchKeyException, KeyTypeMismatch {
        faces = map.getProperty(FACES);
        for (Face face : faces) {
            assertThat(face.getProperty(NoiseWall.FACE_WALL_KEY).value, anyOf(is(true), is(false)));
            for (Coord c : face.getBorderVertices()) {
                assertThat(c.getProperty(NoiseWall.VERTEX_WALL_KEY).value, anyOf(is(true), is(false)));
            }
            assertThat(face.getCenter().getProperty(NoiseWall.VERTEX_WALL_KEY).value, anyOf(is(true), is(false)));
        }
    }

    @Test
    @Ignore
    public void printCaveOutline() throws NoSuchKeyException, KeyTypeMismatch {
        faces = map.getProperty(FACES);
        final BufferedImage image = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_USHORT_GRAY);
        short[] data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
        for (Face face : faces) {
            if (face.getProperty(NoiseWall.FACE_WALL_KEY).value) {
                data[(int) ((face.getCenter().y * mapSize) + face.getCenter().x)] = (short) 16000;
            } else {
                data[(int) ((face.getCenter().y * mapSize) + face.getCenter().x)] = (short) 65535;
            }
        }
        try {
            ImageIO.write(image, "PNG", new File("caveOutline.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
