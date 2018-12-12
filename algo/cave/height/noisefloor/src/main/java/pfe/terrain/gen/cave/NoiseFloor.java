package pfe.terrain.gen.cave;

import com.flowpowered.noise.module.source.Perlin;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.*;
import java.util.stream.Collectors;

public class NoiseFloor extends Contract {

    static final Param<Double> FLOOR_ROUGHNESS_PARAM = Param.generateDefaultDoubleParam(
            "floorRoughness", "The roughness of the cave floor", 0.5, "Cave floor roughness"
    );

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(FLOOR_ROUGHNESS_PARAM);
    }

    private static final Key<BooleanType> FACE_WALL_KEY =
            new Key<>(FACES_PREFIX + "IS_WALL", BooleanType.class);

    private static final Key<BooleanType> VERTEX_WALL_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    private static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "CAVE_HEIGHT", "height", DoubleType.class);

    private static final Key<Double> WALL_HEIGHT_KEY = new Key<>("WALL_HEIGHT", Double.class);

    private static final Key<MarkerType> FLOOR_KEY = new Key<>("FLOOR_CAVE", MarkerType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, VERTICES, VERTEX_WALL_KEY, SIZE, WALL_HEIGHT_KEY, FACE_WALL_KEY),
                asKeySet(FLOOR_KEY),
                asKeySet(HEIGHT_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Creates a random floor elevation";
    }

    private static final Comparator<Coord> HEIGHT_COMPARATOR =
            (a, b) -> (int) (1000 * (a.getProperty(HEIGHT_KEY).value - b.getProperty(HEIGHT_KEY).value));

    @Override
    public void execute(TerrainMap map, Context context) {
        double maxHeight = map.getProperty(WALL_HEIGHT_KEY);
        double minHeight = findMinHeight(map);
        Set<Coord> emptyVertices = map.getProperty(VERTICES).stream()
                .filter(vertex -> !vertex.getProperty(VERTEX_WALL_KEY).value)
                .collect(Collectors.toSet());
        int size = map.getProperty(SIZE);
        double floorRoughness = context.getParamOrDefault(FLOOR_ROUGHNESS_PARAM);

        Perlin noise = new Perlin();
        noise.setSeed(map.getProperty(SEED));
        noise.setFrequency(floorRoughness);
        noise.setLacunarity(1.06);
        noise.setPersistence(1.05);
        noise.setOctaveCount(8);

        Map<Coord, Double> generatedValues = new HashMap<>();
        for (Coord vertex : emptyVertices) {
            double value = noise.getValue(2 * (vertex.x / size - 0.5), 2 * (vertex.y / size - 0.5), 0) + 10;
            generatedValues.put(vertex, value);
        }

        generatedValues.forEach((key, value) ->
                generatedValues.put(key, 1000 * value)
        );

        double minGenerated = Collections.min(generatedValues.values());
        double maxGenerated = Collections.max(generatedValues.values());
        generatedValues.forEach((key, value) ->
                generatedValues.put(key, (maxHeight - minHeight) * (value - minGenerated) / (maxGenerated - minGenerated) + minHeight)
        );

        for (Coord vertex : emptyVertices) {
            vertex.putProperty(HEIGHT_KEY, new DoubleType(generatedValues.get(vertex)));
        }

        map.putProperty(FLOOR_KEY, new MarkerType());
    }

    private double findMinHeight(TerrainMap map) {
        Optional<Coord> min = map.getProperty(VERTICES).stream().min(HEIGHT_COMPARATOR);
        if (!min.isPresent()) {
            throw new RuntimeException("No vertices in the map");
        }
        return min.get().getProperty(HEIGHT_KEY).value;
    }

}
