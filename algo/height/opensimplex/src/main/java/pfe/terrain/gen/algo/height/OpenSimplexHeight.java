package pfe.terrain.gen.algo.height;

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

import java.util.Set;

public class OpenSimplexHeight extends Contract {

    public static final Key<BooleanType> vertexBorderKey =
            new Key<>(verticesPrefix + "IS_BORDER", BooleanType.class);
    public static final Key<BooleanType> faceBorderKey =
            new Key<>(facesPrefix + "IS_BORDER", BooleanType.class);

    public static final Key<DoubleType> vertexHeightKey =
            new SerializableKey<>(verticesPrefix + "HEIGHT", "height", DoubleType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(faces, vertices, vertexBorderKey, faceBorderKey, size, seed),
                asKeySet(vertexHeightKey)
        );
    }

    public static final Param<Double> nbIsland = new Param<>("nbIsland", Double.class, "0-1",
            "The amount of mountains that will be generated. Higher values mean more mountains.", 0.0);

    public static final Param<Double> seaLevel = new Param<>("seaLevel", Double.class, "0-1",
            "The height of the sea level. Higher values mean less land will emerge.", 0.42857);

    public static final Param<Integer> nbSimplexPasses = new Param<>("roughness", Integer.class, "0-10",
            "The number of passes of noise to generate. Less passes mean less variation in terrain height.", 5);

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(nbIsland, seaLevel, nbSimplexPasses);
    }

    // default = 0.005
    private static final double MIN_FREQ = 0.0035;
    private static final double MAX_FREQ = 0.01;

    // default = 120
    private static final double MIN_SEA = 60;
    private static final double MAX_SEA = 200;

    @Override
    public void execute(IslandMap map, Context context)
            throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        //double frequency = (MAX_FREQ - MIN_FREQ) * (context.getParamOrDefault(nbIsland)) + MIN_FREQ;
        double frequency = 0.004;
        NoiseMap elevation = new NoiseMap(map.getVertices(), map.getSize());

        int passes = context.getParamOrDefault(nbSimplexPasses);
        double total = 0;
        for (int i = 0; i < passes; i++) {
            elevation.addSimplexNoise(588 + i, 1 / Math.pow(2, i), frequency / Math.pow(2, i));
            total += 1 / Math.pow(2, i);
        }

        elevation.multiplyHeights(1 / total);

        double sea = (MAX_SEA - MIN_SEA) * (context.getParamOrDefault(seaLevel)) + MIN_SEA;

        elevation.redistribute(1.17);
        elevation.putValuesInRange();
        elevation.multiplyHeights(2.5);
        elevation.lower(sea);
        elevation.putHeightProperty();

        for (Face face : map.getFaces()) {
            if (face.getProperty(faceBorderKey).value) {
                for (Coord coord : face.getBorderVertices()) {
                    if (coord.getProperty(vertexHeightKey).value > 0) {
                        coord.putProperty(vertexHeightKey, new DoubleType(0.0));
                    }
                }
            }
        }

        for (Face face : map.getFaces()) {
            face.getCenter().putProperty(vertexHeightKey, new DoubleType(getAverageHeight(face)));
        }

    }

    private double getAverageHeight(Face face) throws NoSuchKeyException, KeyTypeMismatch {
        double average = 0;
        for (Coord coord : face.getBorderVertices()) {
            average += coord.getProperty(vertexHeightKey).value;
        }
        return average / face.getBorderVertices().size();
    }

}
