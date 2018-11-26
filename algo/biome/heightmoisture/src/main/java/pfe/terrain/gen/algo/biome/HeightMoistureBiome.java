package pfe.terrain.gen.algo.biome;

import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HeightMoistureBiome extends Contract {

    public static final Key<BooleanType> faceWaterKey =
            new Key<>(facesPrefix + "IS_WATER", BooleanType.class);

    public static final Key<DoubleType> heightKey =
            new Key<>(verticesPrefix + "HEIGHT", DoubleType.class);

    public static final Key<WaterKind> waterKindKey =
            new SerializableKey<>(facesPrefix + "WATER_KIND", "waterKind", WaterKind.class);

    public static final Key<Biome> faceBiomeKey =
            new SerializableKey<>(facesPrefix + "BIOME", "biome", Biome.class);

    private final Key<DoubleType> faceMoisture = new Key<>(facesPrefix + "HAS_MOISTURE", DoubleType.class);


    @Override
    public Constraints getContract() {
        return new Constraints(
                asSet(faces, faceWaterKey, faceMoisture, heightKey, waterKindKey),
                asSet(faceBiomeKey)
        );
    }

    @Override
    public void execute(IslandMap map, Context context)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        Map<Face, Double> facesHeight = new HashMap<>();
        WhittakerDiagram diagram = new WhittakerDiagram(WhittakerDiagram.WCLASSIC, 0.9);

        // Normalizing emerged faces between 0 and 1
        for (Face face : map.getFaces()) {
            if (!face.getProperty(faceWaterKey).value) {
                facesHeight.put(face, face.getCenter().getProperty(heightKey).value);
            }
        }
        double maxV = Collections.max(facesHeight.values());
        double minV = Collections.min(facesHeight.values());
        facesHeight.replaceAll((key, val) -> ((val - minV) / (maxV - minV)));

        for (Face face : map.getFaces()) {
            Biome biome = getWaterBiomeIfPresent(face);
            if (biome == null) {
                biome = diagram.getBiome(face.getProperty(faceMoisture).value, facesHeight.get(face));
            }
            face.putProperty(faceBiomeKey, biome);
        }
    }

    private Biome getWaterBiomeIfPresent(Face face) throws NoSuchKeyException, KeyTypeMismatch {
        if (face.getProperty(faceWaterKey).value) {
            if (face.getProperty(waterKindKey) == WaterKind.OCEAN) {
                return Biome.OCEAN;
            } else {
                return Biome.LAKE;
            }
        }
        return null;
    }

}
