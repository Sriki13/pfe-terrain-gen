package pfe.terrain.gen;

import com.flowpowered.noise.module.source.Perlin;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.SerializableKey;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;
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

public class RidgedMoistureTest {

    protected final Key<BooleanType> faceWaterKey = new SerializableKey<>(facesPrefix + "IS_WATER", "isWater", BooleanType.class);


    @Test
    public void checkInterval() throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        RidgedMoisture ridgedMoisture = new RidgedMoisture();
        FaceSet faces = new FaceSet();
        int mapSize = 256;
        generateFaces(faces, mapSize);
        Map<Face, Double> noiseValues = ridgedMoisture.computeNoise(0, faces, mapSize, 1.0, 0.0, 1.0);
        assertThat(noiseValues.keySet().size(), equalTo(faces.size()));
        noiseValues.forEach((key, value) ->
                assertThat(value, is(both(greaterThanOrEqualTo(0.0)).and(lessThanOrEqualTo(1.0))))
        );

    }

    public void generateFaces(Set<Face> faces, int mapSize) throws DuplicateKeyException {
        for (float i = 1; i < mapSize; i += 2) {
            for (float j = 1; j < mapSize; j += 2) {
                Face face = new Face(new Coord(i, j), new HashSet<>());
                face.putProperty(faceWaterKey, new BooleanType(true));
                faces.add(face);
            }
        }
    }
}
