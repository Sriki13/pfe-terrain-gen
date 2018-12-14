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
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.*;

public class HeightMoistureBiome extends Contract {

    public static final Key<BooleanType> FACE_WATER_KEY =
            new Key<>(FACES_PREFIX + "IS_WATER", BooleanType.class);

    public static final Key<DoubleType> HEIGHT_KEY =
            new Key<>(VERTICES_PREFIX + "HEIGHT", DoubleType.class);

    public static final Key<WaterKind> WATER_KIND_KEY =
            new SerializableKey<>(FACES_PREFIX + "WATER_KIND", "waterKind", WaterKind.class);

    public static final Key<Biome> FACE_BIOME_KEY =
            new SerializableKey<>(FACES_PREFIX + "BIOME", "biome", Biome.class);

    private final Param<String> BIOME_STYLE_PARAM = new Param<>("biomeStyle", String.class,
            Arrays.toString(BiomeStyle.values()), "Style of biome repartition", "classic", "Biome repartition");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(BIOME_STYLE_PARAM);
    }

    private final Key<DoubleType> faceMoisture = new Key<>(FACES_PREFIX + "MOISTURE", DoubleType.class);


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, FACE_WATER_KEY, faceMoisture, HEIGHT_KEY, WATER_KIND_KEY),
                asKeySet(FACE_BIOME_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Creates biomes relative to the height and moisture of tiles, " +
                "there are some presets to create different biomes repartitions";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        Map<Face, Double> facesHeight = new HashMap<>();
        String styleName = context.getParamOrDefault(BIOME_STYLE_PARAM);
        BiomeStyle style;
        style = BiomeStyle.valueOf(styleName.toUpperCase());
        WhittakerDiagram diagram = new WhittakerDiagram(style.getWhit());
        // Normalizing emerged faces between 0 and 1
        for (Face face : map.getProperty(FACES)) {
            if (!face.getProperty(FACE_WATER_KEY).value) {
                facesHeight.put(face, face.getCenter().getProperty(HEIGHT_KEY).value);
            }
        }
        if (facesHeight.isEmpty()) {
            // Nice ocean you got there dude
            for (Face face : map.getProperty(FACES)) {
                face.putProperty(FACE_BIOME_KEY, Biome.OCEAN);
            }
            return;
        }
        double maxV = Collections.max(facesHeight.values());
        double minV = Collections.min(facesHeight.values());
        facesHeight.replaceAll((key, val) -> ((val - minV) / (maxV - minV)));

        for (Face face : map.getProperty(FACES)) {
            Biome biome = getWaterBiomeIfPresent(face);
            if (biome == null) {
                biome = diagram.getBiome(face.getProperty(faceMoisture).value, facesHeight.get(face));
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

}
