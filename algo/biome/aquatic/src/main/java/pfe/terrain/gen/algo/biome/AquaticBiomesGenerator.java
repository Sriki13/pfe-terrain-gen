package pfe.terrain.gen.algo.biome;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.OptionalKey;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.AquaticBiome;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.*;
import java.util.stream.Collectors;

public class AquaticBiomesGenerator extends Contract {

    public static final Param<Integer> NB_REEFS = Param.generatePositiveIntegerParam(
            "nbCoralReef", 10, "The number of coral reefs that will spawn on the island.",
            2, "Number of coral reefs"
    );

    public static Param<Integer> MAX_REEF_SIZE = Param.generatePositiveIntegerParam(
            "reefMaxSize", 10, "The maximum number of faces a coral reef may spread to. Multiple reef may still spawn close" +
                    "to one other and fuse, creating reefs with a size over this limit.",
            2, "Coral reef maximum size"
    );

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(NB_REEFS, MAX_REEF_SIZE);
    }

    public static final SerializableKey<AquaticBiome> AQUATIC_BIOME_KEY =
            new SerializableKey<>(new OptionalKey<>(FACES_PREFIX + "AQUATIC_BIOME", AquaticBiome.class), "aquaBiome");

    public static final Key<DoubleType> HEIGHT_KEY =
            new Key<>(VERTICES_PREFIX + "HEIGHT", DoubleType.class);

    public static final Key<BooleanType> IS_WATER_KEY =
            new Key<>(FACES_PREFIX + "IS_WATER", BooleanType.class);

    public static final Key<WaterKind> WATER_KIND_KEY =
            new Key<>(FACES_PREFIX + "WATER_KIND", WaterKind.class);

    public static final Key<MarkerType> OCEAN_FLOOR_KEY =
            new Key<>("OCEAN_HEIGHT", MarkerType.class);


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, SEED, HEIGHT_KEY, OCEAN_FLOOR_KEY, IS_WATER_KEY),
                asKeySet(AQUATIC_BIOME_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Generates aquatic biomes if a map has a sea floor. Can also generate coral reefs";
    }

    public static final double SHALLOW_MIN = 0.8;
    public static final double DEEP_MAX = 0.3;


    @Override
    public void execute(TerrainMap map, Context context) {
        Map<Face, Double> normalized = normalizeHeights(map);
        assignBiomes(normalized);
        spawnReef(context, map.getProperty(SEED), normalized.keySet().stream()
                .filter(face -> face.getProperty(AQUATIC_BIOME_KEY) == AquaticBiome.SHALLOW_WATER)
                .collect(Collectors.toList()));
    }

    private Map<Face, Double> normalizeHeights(TerrainMap map) {
        Set<Face> oceanFaces = map.getProperty(FACES).stream()
                .filter(f -> f.getProperty(IS_WATER_KEY).value && f.getProperty(WATER_KIND_KEY) == WaterKind.OCEAN)
                .collect(Collectors.toSet());
        double minHeight = Collections.min(oceanFaces,
                (a, b) -> (int) (a.getCenter().getProperty(HEIGHT_KEY).value - b.getCenter().getProperty(HEIGHT_KEY).value))
                .getCenter().getProperty(HEIGHT_KEY).value;
        Map<Face, Double> normalized = new HashMap<>();
        for (Face face : oceanFaces) {
            normalized.put(face, ((face.getCenter().getProperty(HEIGHT_KEY).value - minHeight) / -minHeight));
        }
        return normalized;
    }

    private void assignBiomes(Map<Face, Double> normalized) {
        for (Map.Entry<Face, Double> entry : normalized.entrySet()) {
            double height = entry.getValue();
            if (height > SHALLOW_MIN) {
                putBiome(entry.getKey(), AquaticBiome.SHALLOW_WATER);
            } else if (height > DEEP_MAX) {
                putBiome(entry.getKey(), AquaticBiome.OCEAN);
            } else {
                putBiome(entry.getKey(), AquaticBiome.DEEP_OCEAN);
            }
        }
    }

    private void spawnReef(Context context, int seed, List<Face> shallowWaters) {
        Random random = new Random(seed);
        int nbReefs = context.getParamOrDefault(NB_REEFS);
        int reefSize = context.getParamOrDefault(MAX_REEF_SIZE);
        Set<Face> starts = new HashSet<>();
        for (int i = 0; i < nbReefs; i++) {
            if (shallowWaters.isEmpty()) {
                break;
            }
            Face coral = shallowWaters.get(random.nextInt(shallowWaters.size()));
            shallowWaters.remove(coral);
            starts.add(coral);
        }
        for (Face coral : starts) {
            spreadReef(random, coral, reefSize, 1);
        }
    }

    private void spreadReef(Random random, Face start, int max, int count) {
        putBiome(start, AquaticBiome.CORAL_REEF);
        if (max == count) {
            return;
        }
        List<Face> spreadCandidates = start.getNeighbors().stream()
                .filter(face -> face.hasProperty(AQUATIC_BIOME_KEY) &&
                        face.getProperty(AQUATIC_BIOME_KEY) == AquaticBiome.SHALLOW_WATER)
                .collect(Collectors.toList());
        if (spreadCandidates.isEmpty()) {
            return;
        }
        spreadReef(random, spreadCandidates.get(random.nextInt(spreadCandidates.size())), max, count + 1);
    }

    private void putBiome(Face face, AquaticBiome biome) {
        face.putProperty(AQUATIC_BIOME_KEY, biome);
    }

}
