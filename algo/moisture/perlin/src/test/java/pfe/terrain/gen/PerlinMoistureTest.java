package pfe.terrain.gen;

import com.flowpowered.noise.module.source.Perlin;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;
import pfe.terrain.gen.algo.key.Key;
import pfe.terrain.gen.algo.key.SerializableKey;
import pfe.terrain.gen.algo.types.BooleanType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static pfe.terrain.gen.algo.constraints.Contract.facesPrefix;

public class PerlinMoistureTest {

    protected final Key<BooleanType> faceWaterKey = new SerializableKey<>(facesPrefix + "IS_WATER", "isWater", BooleanType.class);


    @Test
    public void checkInterval() throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        PerlinMoisture perlinMoisture = new PerlinMoisture();
        FaceSet faces = new FaceSet();
        int mapSize = 256;
        generateFaces(faces, mapSize);
        Map<Face, Double> noiseValues = perlinMoisture.computeNoise(0, faces, mapSize, 1.0, 0.0, 1.0);
        assertThat(noiseValues.keySet().size(), equalTo(faces.size()));
        noiseValues.forEach((key, value) ->
                assertThat(value, is(both(greaterThanOrEqualTo(0.0)).and(lessThanOrEqualTo(1.0))))
        );
    }

    @Test
    @Ignore
    public void printPerlin() throws DuplicateKeyException {
        Set<Face> faces = new HashSet<>();
        int mapSize = 256;
        generateFaces(faces, mapSize);
        final Perlin perlin = new Perlin();
        perlin.setSeed(1);
        perlin.setFrequency(1.0);
        List<Double> values = new ArrayList<>(Collections.nCopies(mapSize * mapSize, 1.0));
        for (Face face : faces) {
            final double noise = perlin.getValue(face.getCenter().x / mapSize, face.getCenter().y / mapSize, 0);
            values.set((int) (Math.floor(face.getCenter().y * mapSize) + face.getCenter().x), noise);
        }
        double max = Collections.max(values);
        double min = Collections.min(values);
        values.replaceAll(val -> (val - min) / (max - min));
        final BufferedImage image = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_USHORT_GRAY);
        short[] data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) != null) {
                data[i] = (short) Math.round(values.get(i) * 65535);
            }
        }
        try {
            ImageIO.write(image, "PNG", new File("noise.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateFaces(Set<Face> faces, int mapSize) throws DuplicateKeyException {
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                Face face = new Face(new Coord(i, j), new HashSet<>());
                face.putProperty(faceWaterKey, new BooleanType(false));
                faces.add(face);
            }
        }
    }
}
