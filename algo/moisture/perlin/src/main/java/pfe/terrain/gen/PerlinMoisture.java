package pfe.terrain.gen;

import com.flowpowered.noise.module.source.Perlin;
import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class PerlinMoisture extends Contract {

    private final SerializableKey<DoubleType> faceMoisture = new SerializableKey<>(facesPrefix + "HAS_MOISTURE", "moisture", DoubleType.class);

    private final Key<BooleanType> faceWaterKey = new SerializableKey<>(facesPrefix + "IS_WATER", "isWater", BooleanType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(faces, seed, faceWaterKey), asKeySet(faceMoisture));
    }

    private final Param<Double> minMoisture = new Param<>("minMoisture", Double.class,
            "0-1", "Minimal Moisture (0.5 means a humid island, 1.0 means all map will have max moisture", 0.0);
    private final Param<Double> maxMoisture = new Param<>("maxMoisture", Double.class,
            "0-1", "Maximal Moisture (0.5 means a arid island, 0.0 means all map will have min moisture", 1.0);
    private final Param<Double> biomeQuantity = new Param<>("biomeQuantity", Double.class,
            "0-1", "", 0.25);

    public Set<Param> getRequestedParameters() {
        return asParamSet(minMoisture, maxMoisture, biomeQuantity);
    }

    @Override
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, KeyTypeMismatch, NoSuchKeyException {
        FaceSet faces = map.getFaces();
        int mapSize = map.getSize();
        double frequency = context.getParamOrDefault(biomeQuantity);
        double min = context.getParamOrDefault(minMoisture);
        double max = context.getParamOrDefault(maxMoisture);
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
