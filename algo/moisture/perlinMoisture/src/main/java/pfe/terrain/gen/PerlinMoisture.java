package pfe.terrain.gen;

import com.flowpowered.noise.module.source.Perlin;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.algorithms.MoistureGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class PerlinMoisture extends MoistureGenerator {

    private Key<Double> biomeQuantity = new Key<>("biomeQuantity", Double.class);

    public Set<Key> getRequestedParameters() {
        Set<Key> keys = super.getRequestedParameters();
        keys.add(biomeQuantity);
        return keys;
    }

    @Override
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, KeyTypeMismatch, NoSuchKeyException {
        FaceSet faces = map.getFaces();
        int mapSize = map.getSize();
        double frequency = context.getPropertyOrDefault(biomeQuantity, 0.25);
        double min = context.getPropertyOrDefault(minMoisture, 0.0);
        double max = context.getPropertyOrDefault(maxMoisture, 1.0);
        if (max < min) {
            Logger.getLogger(this.getName()).warning("Max moisture is bigger than min moisture, going with default values");
            min = 0.0;
            max = 1.0;
        }
        Map<Face, Double> noiseValues = computeNoise(map.getSeed(), faces, mapSize, frequency, min, max);
        for (Face face : faces) {
            face.putProperty(faceMoisture, new DoubleType(noiseValues.get(face)));
        }
    }

    Map<Face, Double> computeNoise(int seed, FaceSet faces, int mapSize, double frequency, double min, double max) throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        Perlin perlin = new Perlin();
        perlin.setSeed(seed);
        perlin.setFrequency(frequency * 9 + 1);
        Map<Face, Double> noiseValue = new HashMap<>();
        for (Face face : faces) {
            if (face.getProperty(faceWaterKey).value) {
                face.putProperty(faceMoisture, new DoubleType(1.0));
            }
            Coord c = face.getCenter();
            noiseValue.put(face, perlin.getValue(c.x / mapSize, c.y / mapSize, 0));
        }
        double maxV = Collections.max(noiseValue.values());
        double minV = Collections.min(noiseValue.values());
        noiseValue.replaceAll((key, val) -> ((val - minV) / (maxV - minV)) * (max - min) + min);
        return noiseValue;
    }
}
