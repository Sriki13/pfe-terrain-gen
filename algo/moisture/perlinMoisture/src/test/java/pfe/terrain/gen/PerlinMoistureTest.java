package pfe.terrain.gen;

import com.flowpowered.noise.module.source.Perlin;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PerlinMoistureTest {

    @Test
    public void checkInterval() {
        PerlinMoisture perlinMoisture = new PerlinMoisture();
        FaceSet faces = new FaceSet();
        int mapSize = 1024;
        for (float i = 1; i < mapSize; i += 9) {
            for (float j = 1; j < mapSize; j += 9) {
                faces.add(new Face(new Coord(i, j), new HashSet<>()));
            }
        }
        Map<Face, Double> noiseValues = perlinMoisture.computeNoise(0, faces, mapSize, 1.0);
        assertThat(noiseValues.keySet().size(), equalTo(faces.size()));
        noiseValues.forEach((key, value) ->
                assertThat(value, is(both(greaterThanOrEqualTo(0.0)).and(lessThanOrEqualTo(1.0))))
        );

    }

    @Test
    @Ignore
    public void printPerlin() {
        Set<Face> faces = new HashSet<>();
        int mapSize = 1024;
        for (float i = 1; i < mapSize; i += 9) {
            for (float j = 1; j < mapSize; j += 9) {
                faces.add(new Face(new Coord(i, j), new HashSet<>()));
            }
        }
        final Perlin perlin = new Perlin();
        perlin.setSeed(1);
        perlin.setFrequency(5.0);
        List<Double> values = new ArrayList<>(Collections.nCopies(mapSize * mapSize, 1.0));
        for (Face face : faces) {
            final double noise = perlin.getValue(face.getCenter().x / mapSize, face.getCenter().y / mapSize, 0);
            values.set((int) (Math.floor(face.getCenter().y * mapSize) + face.getCenter().x), noise);
            values.set((int) (Math.floor(face.getCenter().y * mapSize) + face.getCenter().x + 1), noise);
            values.set((int) (Math.floor((face.getCenter().y + 1) * mapSize) + face.getCenter().x), noise);
            values.set((int) (Math.floor((face.getCenter().y + 1) * mapSize) + face.getCenter().x + 1), noise);
            values.set((int) (Math.floor((face.getCenter().y - 1) * mapSize) + face.getCenter().x), noise);
            values.set((int) (Math.floor((face.getCenter().y - 1) * mapSize) + face.getCenter().x + 1), noise);
            values.set((int) (Math.floor(face.getCenter().y * mapSize) + face.getCenter().x - 1), noise);
            values.set((int) (Math.floor((face.getCenter().y + 1) * mapSize) + face.getCenter().x - 1), noise);
            values.set((int) (Math.floor((face.getCenter().y - 1) * mapSize) + face.getCenter().x - 1), noise);
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
}
