package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Arrays;
import java.util.Set;

public class SimplexHeight extends Contract {

    public static final Key<BooleanType> vertexBorderKey =
            new Key<>(verticesPrefix + "IS_BORDER", BooleanType.class);
    public static final Key<BooleanType> faceBorderKey =
            new Key<>(facesPrefix + "IS_BORDER", BooleanType.class);

    public static final Key<DoubleType> vertexHeightKey =
            new SerializableKey<>(verticesPrefix + "HEIGHT", "height", DoubleType.class);
    public static final Key<Void> oceanFloorKey =
            new Key<>("OCEAN_HEIGHT", Void.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(faces, vertices, vertexBorderKey, faceBorderKey, size, seed),
                asKeySet(vertexHeightKey)
        );
    }

    public static final Param<Double> simplexIslandSize = new Param<>("simplexIslandSize", Double.class, "0-1",
            "The size of ths islands that will be generated. Higher values mean bigger islands.", 0.333333, "Island size");

    public static final Param<Double> seaLevelParam = new Param<>("seaLevel", Double.class, "0-1",
            "The height of the sea level. Higher values mean less land will emerge.", 0.444444, "Sea level");

    public static final Param<Double> nbSimplexPasses = new Param<>("smoothness", Double.class, "0-1",
            "How smooth the terrain should be. Lower values mean smoother terrain.", 0.333333, "Height smoothness");

    public static final Param<String> islandShape = new Param<>("simplexShape", String.class,
            Arrays.toString(SimplexIslandShape.values()),
            "The general shape of the coasts of the island.", "SQUARE", "Coast general shape");

    public static final Param<Double> simplexNbIsland = new Param<>("simplexNbIsland", Double.class, "0-1",
            "The number of islands that will be generated. Higher values mean more islands.", 0.0, "Number of islands");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(simplexIslandSize, seaLevelParam, nbSimplexPasses, islandShape, simplexNbIsland);
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
    public void execute(IslandMap map, Context context) {

        SimplexNoiseMap elevation = new SimplexNoiseMap(map.getVertices(), map.getSize(), map.getSeed());
        double passes = (MAX_PASSES - MIN_PASSES) * (context.getParamOrDefault(nbSimplexPasses)) + MIN_PASSES;
        double islandSize = (MAX_SIZE - MIN_SIZE) * (context.getParamOrDefault(simplexIslandSize)) + MIN_SIZE;
        double seaLevel = (MAX_SEA - MIN_SEA) * (context.getParamOrDefault(seaLevelParam)) + MIN_SEA;
        double factor = (MAX_NB - MIN_NB) * (context.getParamOrDefault(simplexNbIsland)) + MIN_NB;

        double total = 0;
        SimplexIslandShape shape = SimplexIslandShape.getFromString(context.getParamOrDefault(islandShape));

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

        for (Face face : map.getFaces()) {
            face.getCenter().putProperty(vertexHeightKey, new DoubleType(getAverageHeight(face)));
        }
        map.putProperty(oceanFloorKey, null);
    }

    private double getAverageHeight(Face face) throws NoSuchKeyException, KeyTypeMismatch {
        double average = 0;
        for (Coord coord : face.getBorderVertices()) {
            average += coord.getProperty(vertexHeightKey).value;
        }
        return average / face.getBorderVertices().size();
    }

}
