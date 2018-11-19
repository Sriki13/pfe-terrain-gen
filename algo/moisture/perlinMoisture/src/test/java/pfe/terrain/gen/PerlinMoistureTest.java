package pfe.terrain.gen;

import com.flowpowered.noise.module.source.Perlin;
import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Test;
import pfe.terrain.gen.algo.geometry.Face;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PerlinMoistureTest {

    @Test
    public void getFaceByCenterTest() {
        Set<Face> faces = new HashSet<>();
        for (float i = 0; i < 1; i += 0.025) {
            for (float j = 0; j < 1; j += 0.025) {
                faces.add(new Face(new Coordinate(i, j), new ArrayList<>()));
            }
        }
//        for (Face face : faces) {
//            Coordinate c = face.getCenter();
//            System.out.println(c);
//            System.out.println(perlin.getValue(c.x, c.y, 0));
//        }
        final int width = 64;
        final Perlin perlin = new Perlin();
        perlin.setSeed(1);
        perlin.setFrequency(10.0);

        final BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_USHORT_GRAY);
        short[] data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
        List<Double> values = new ArrayList<>(width * width);
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                final double noise = perlin.getValue(x / (float) width, y / (float) width, 0);
                //System.out.println(noise);
                values.add(y * width + x, noise);
                //data[y * width + x] = noise;
            }
        }
        double max = Collections.max(values);
        double min = Collections.min(values);
        values.replaceAll(val -> (val - min) / (max - min));
        //System.out.println(values);
        for (int i = 0; i < values.size(); i++) {
            data[i] = (short) Math.round(values.get(i) * 65536);
        }
        try {
            ImageIO.write(image, "PNG", new File("noise.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
