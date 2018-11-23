package pfe.terrain.gen.algo.biome;

import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import static pfe.terrain.gen.algo.Biome.*;

public class HeightBiomes extends Contract {

    public static final Key<BooleanType> faceWaterKey =
            new Key<>(facesPrefix + "IS_WATER", BooleanType.class);

    public static final Key<DoubleType> heightKey =
            new Key<>(verticesPrefix + "HEIGHT", DoubleType.class);

    public static final Key<WaterKind> waterKindKey =
            new SerializableKey<>(facesPrefix + "WATER_KIND", "waterKind", WaterKind.class);


    public static final Key<Biome> faceBiomeKey =
            new SerializableKey<>(facesPrefix + "BIOME", "biome", Biome.class);


    @Override
    public Constraints getContract() {
        return new Constraints(
                asSet(faces, faceWaterKey, heightKey, waterKindKey),
                asSet(faceBiomeKey)
        );
    }

    @Override
    public void execute(IslandMap map, Context context)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        for (Face face : map.getFaces()) {
            Biome biome = getWaterBiomeIfPresent(face);
            if (biome == null) {
                biome = getBiomeFromElevation(face);
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

    private Biome getBiomeFromElevation(Face face) throws NoSuchKeyException, KeyTypeMismatch {
        double elevation = 0;
        for (Coord vertex : face.getVertices()) {
            elevation += vertex.getProperty(heightKey).value;
        }
        elevation = elevation / face.getVertices().size();
        if (elevation < 2) return BEACH;
        if (elevation < 6) return GRASSLAND;
        if (elevation < 10) return TEMPERATE_RAIN_FOREST;
        if (elevation < 14) return SHRUBLAND;
        if (elevation < 16) return ALPINE;
        return SNOW;
    }

}
