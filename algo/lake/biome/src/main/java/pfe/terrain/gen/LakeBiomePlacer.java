package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.Biome;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LakeBiomePlacer extends Contract {

    public static final Param<Double> LAKE_ICE_LEVEL = Param.generateDefaultDoubleParam("lakeIceLevel",
            "The height above which lakes freeze.", 0.7, "Lake ice level");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(LAKE_ICE_LEVEL);
    }

    public static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    public static final Key<Biome> FACE_BIOME_KEY =
            new SerializableKey<>(FACES_PREFIX + "BIOME", "biome", Biome.class);

    public static final Key<BooleanType> VERTEX_WATER_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    public static final Key<WaterKind> WATER_KIND_KEY =
            new SerializableKey<>(FACES_PREFIX + "WATER_KIND", "waterKind", WaterKind.class);

    public static final Key<MarkerType> HAS_LAKES_KEY = new Key<>("LAKES", MarkerType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, HEIGHT_KEY, VERTEX_WATER_KEY, WATER_KIND_KEY, HAS_LAKES_KEY),
                asKeySet(),
                asKeySet(FACE_BIOME_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Marks lakes as the correct biome: lake or glacier, depending on height";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        List<Coord> landPoints = map.getProperty(VERTICES).stream()
                .filter(c -> !c.getProperty(VERTEX_WATER_KEY).value)
                .sorted((a, b) -> (int) (1000 * (a.getProperty(HEIGHT_KEY).value - b.getProperty(HEIGHT_KEY).value)))
                .collect(Collectors.toList());
        double iceLevel = landPoints.get((int) (context.getParamOrDefault(LAKE_ICE_LEVEL) * (landPoints.size() - 1)))
                .getProperty(HEIGHT_KEY).value;
        map.getProperty(FACES).stream()
                .filter(f -> f.getProperty(WATER_KIND_KEY) == WaterKind.LAKE)
                .forEach(f -> {
                    if (f.getCenter().getProperty(HEIGHT_KEY).value >= iceLevel) {
                        f.putProperty(FACE_BIOME_KEY, Biome.GLACIER);
                    } else {
                        f.putProperty(FACE_BIOME_KEY, Biome.LAKE);
                    }
                });
    }

}
