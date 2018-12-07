package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.OptionalKey;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.Arrays;
import java.util.Set;

public class SimplexHeight extends Contract {

    // Required

    public static final Key<MarkerType> VERTEX_BORDER_KEY =
            new OptionalKey<>(VERTICES_PREFIX + "IS_BORDER", MarkerType.class);

    public static final Key<MarkerType> FACE_BORDER_KEY =
            new OptionalKey<>(FACES_PREFIX + "IS_BORDER", MarkerType.class);

    // Produced

    public static final Key<DoubleType> VERTEX_HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    public static final Key<MarkerType> OCEAN_FLOOR_KEY =
            new Key<>("OCEAN_HEIGHT", MarkerType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, VERTICES, VERTEX_BORDER_KEY, FACE_BORDER_KEY, SIZE, SEED),
                asKeySet(VERTEX_HEIGHT_KEY, OCEAN_FLOOR_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Creates a height map with water floor based on the simplex noise, should be used over opensimplex";
    }

    public static final Param<Double> SIMPLEX_ISLAND_SIZE = Param.generateDefaultDoubleParam(
            "simplexIslandSize", "The size of ths islands that will be generated. Higher values mean bigger islands.",
            0.333333, "Island size");

    public static final Param<Double> SEA_LEVEL_PARAM = Param.generateDefaultDoubleParam("seaLevel",
            "The height of the sea level. Higher values mean less land will emerge.", 0.444444, "Sea level");

    public static final Param<Double> NB_SIMPLEX_PASSES = Param.generateDefaultDoubleParam("smoothness",
            "How smooth the terrain should be. Lower values mean smoother terrain.", 0.333333, "Height smoothness");

    public static final Param<String> ISLAND_SHAPE = new Param<>("simplexShape", String.class,
            Arrays.toString(SimplexIslandShape.values()),
            "The general shape of the coasts of the island.", "SQUARE", "Coast general shape");

    public static final Param<Double> SIMPLEX_NB_ISLAND = Param.generateDefaultDoubleParam("simplexNbIsland",
            "The number of islands that will be generated. Higher values mean more islands.", 0.0, "Number of islands");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(SIMPLEX_ISLAND_SIZE, SEA_LEVEL_PARAM, NB_SIMPLEX_PASSES, ISLAND_SHAPE, SIMPLEX_NB_ISLAND);
    }

    // default = 0.05
    private static final double MAX_SIZE = 0.15;
    private static final double MIN_SIZE = 0;

    // default = 10
    private static final int MAX_PASSES = 20;
    private static final int MIN_PASSES = 5;

    // default = 0.7
    private static final double MIN_SEA = 0.5;
    private static final double MAX_SEA = 0.95;

    // default = 1
    private static final double MIN_NB = 1;
    private static final double MAX_NB = 10;

    @Override
    public void execute(TerrainMap map, Context context) {

        SimplexNoiseMap elevation = new SimplexNoiseMap(map.getProperty(VERTICES), map.getProperty(SIZE), map.getProperty(SEED));
        double passes = (MAX_PASSES - MIN_PASSES) * (context.getParamOrDefault(NB_SIMPLEX_PASSES)) + MIN_PASSES;
        double islandSize = (MAX_SIZE - MIN_SIZE) * (context.getParamOrDefault(SIMPLEX_ISLAND_SIZE)) + MIN_SIZE;
        double seaLevel = (MAX_SEA - MIN_SEA) * (context.getParamOrDefault(SEA_LEVEL_PARAM)) + MIN_SEA;
        double factor = (MAX_NB - MIN_NB) * (context.getParamOrDefault(SIMPLEX_NB_ISLAND)) + MIN_NB;

        double total = 0;
        SimplexIslandShape shape = SimplexIslandShape.getFromString(context.getParamOrDefault(ISLAND_SHAPE));

        for (int i = 0; i < passes; i++) {
            elevation.addSimplexNoise(1 / Math.pow(2, i), factor * Math.pow(2, i), islandSize, shape);
            total += 1 / Math.pow(2, i);
        }

        elevation.multiplyHeights(1 / total);
        elevation.redistribute(3);
        elevation.multiplyHeights(40 / factor);

        elevation.setWaterLevel(seaLevel);
        elevation.ensureBordersAreLow();
        elevation.putHeightProperty();

        for (Face face : map.getProperty(FACES)) {
            face.getCenter().putProperty(VERTEX_HEIGHT_KEY, new DoubleType(getAverageHeight(face)));
        }
        map.putProperty(OCEAN_FLOOR_KEY, new MarkerType());
    }

    private double getAverageHeight(Face face) throws NoSuchKeyException, KeyTypeMismatch {
        double average = 0;
        for (Coord coord : face.getBorderVertices()) {
            average += coord.getProperty(VERTEX_HEIGHT_KEY).value;
        }
        return average / face.getBorderVertices().size();
    }

}
