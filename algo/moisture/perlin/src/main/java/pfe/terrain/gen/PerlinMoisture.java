package pfe.terrain.gen;

import com.flowpowered.noise.module.source.Perlin;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class PerlinMoisture extends Contract {

    private final SerializableKey<DoubleType> FACE_MOISTURE =
            new SerializableKey<>(FACES_PREFIX + "HAS_MOISTURE", "moisture", DoubleType.class);

    private final Key<BooleanType> FACE_WATER_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(FACES, SEED, FACE_WATER_KEY), asKeySet(FACE_MOISTURE));
    }

    private final Param<Double> MIN_MOISTURE = Param.generateDefaultDoubleParam("minMoisture",
            "Minimal Moisture (0.5 means a humid island, 1.0 means all map will have max moisture", 0.0, "Minimum moisture");
    private final Param<Double> MAX_MOISTURE = Param.generateDefaultDoubleParam("maxMoisture",
            "Maximal Moisture (0.5 means a arid island, 0.0 means all map will have min moisture", 1.0, "Maximum moisture");
    private final Param<Double> BIOME_QUANTITY = Param.generateDefaultDoubleParam("biomeQuantity",
            "Size of moisture pockets", 0.25, "Size of moisture pockets");

    public Set<Param> getRequestedParameters() {
        return asParamSet(MIN_MOISTURE, MAX_MOISTURE, BIOME_QUANTITY);
    }

    @Override
    public void execute(IslandMap map, Context context) {
        FaceSet faces = map.getFaces();
        int mapSize = map.getSize();
        double frequency = context.getParamOrDefault(BIOME_QUANTITY);
        double min = context.getParamOrDefault(MIN_MOISTURE);
        double max = context.getParamOrDefault(MAX_MOISTURE);
        if (max < min) {
            Logger.getLogger(this.getName()).warning("Max moisture is bigger than min moisture, going with default values");
            min = 0.0;
            max = 1.0;
        }
        Map<Face, Double> noiseValues = computeNoise(map.getSeed(), faces, mapSize, frequency, min, max);
        for (Face face : faces) {
            if (!face.getProperty(FACE_WATER_KEY).value) {
                face.putProperty(FACE_MOISTURE, new DoubleType(noiseValues.get(face)));
            } else {
                face.putProperty(FACE_MOISTURE, new DoubleType(1.0));
            }
        }
    }

    Map<Face, Double> computeNoise(int seed, FaceSet faces, int mapSize, double frequency, double min, double max) throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        Perlin perlin = new Perlin();
        perlin.setSeed(seed);
        perlin.setFrequency(frequency * 9 + 1);
        Map<Face, Double> noiseValue = new HashMap<>();
        for (Face face : faces) {
            if (!face.getProperty(FACE_WATER_KEY).value) {
                Coord c = face.getCenter();
                noiseValue.put(face, perlin.getValue(c.x / mapSize, c.y / mapSize, 0));
            }
        }
        double maxV = Collections.max(noiseValue.values());
        double minV = Collections.min(noiseValue.values());
        noiseValue.replaceAll((key, val) -> ((val - minV) / (maxV - minV)) * (max - min) + min);
        return noiseValue;
    }
}
