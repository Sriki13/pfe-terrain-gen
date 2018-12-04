package pfe.terrain.gen.algo.biome;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.Biome;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Set;

import static pfe.terrain.gen.algo.island.Biome.*;

public class HeightBiomes extends Contract {

    private static final Param<Integer> HEIGHT_STEP_KEY = new Param<>("heightBiomeStep", Integer.class, 1, 25,
            "Average interval between two biomes on the Z-axis", 4, "Average height between 2 biomes");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(HEIGHT_STEP_KEY);
    }

    public static final Key<BooleanType> FACE_WATER_KEY =
            new Key<>(FACES_PREFIX + "IS_WATER", BooleanType.class);

    public static final Key<DoubleType> HEIGHT_KEY =
            new Key<>(VERTICES_PREFIX + "HEIGHT", DoubleType.class);

    public static final Key<WaterKind> WATER_KIND_KEY =
            new SerializableKey<>(FACES_PREFIX + "WATER_KIND", "waterKind", WaterKind.class);

    public static final Key<Biome> FACE_BIOME_KEY =
            new SerializableKey<>(FACES_PREFIX + "BIOME", "biome", Biome.class);


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, FACE_WATER_KEY, HEIGHT_KEY, WATER_KIND_KEY),
                asKeySet(FACE_BIOME_KEY)
        );
    }

    @Override
    public void execute(IslandMap map, Context context) {
        double step = context.getParamOrDefault(HEIGHT_STEP_KEY);
        for (Face face : map.getFaces()) {
            Biome biome = getWaterBiomeIfPresent(face);
            if (biome == null) {
                biome = getBiomeFromElevation(face, step);
            }
            face.putProperty(FACE_BIOME_KEY, biome);
        }
    }

    private Biome getWaterBiomeIfPresent(Face face) throws NoSuchKeyException, KeyTypeMismatch {
        if (face.getProperty(FACE_WATER_KEY).value) {
            if (face.getProperty(WATER_KIND_KEY) == WaterKind.OCEAN) {
                return Biome.OCEAN;
            } else {
                return Biome.LAKE;
            }
        }
        return null;
    }

    private Biome getBiomeFromElevation(Face face, double heightStep)
            throws NoSuchKeyException, KeyTypeMismatch {
        double elevation = 0;
        for (Coord vertex : face.getBorderVertices()) {
            elevation += vertex.getProperty(HEIGHT_KEY).value;
        }
        elevation = elevation / face.getBorderVertices().size();
        if (elevation < heightStep / 2) return BEACH;
        if (elevation < 2 * heightStep) return GRASSLAND;
        if (elevation < 5 * heightStep) return TEMPERATE_RAIN_FOREST;
        if (elevation < 7 * heightStep) return SHRUBLAND;
        if (elevation < 8.5 * heightStep) return ALPINE;
        return SNOW;
    }

}
