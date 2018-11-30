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

import java.util.Set;

public class OpenSimplexHeight extends Contract {

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

    public static final Param<Double> nbIsland = new Param<>("nbIsland", Double.class, "0-1",
            "The amount of islands that will be generated. Higher values mean the map will be an archipelago.", 0.0, "Number of islands");

    public static final Param<Double> seaLevel = new Param<>("seaLevel", Double.class, "0-1",
            "The height of the sea level. Higher values mean less land will emerge.", 0.55, "Sea level");

    public static final Param<Integer> heightMultiplier = new Param<>("heightMultiplier", Integer.class, "0-100",
            "A coefficient that will be applied to all of the generated height values. Higher values will increase the" +
                    " height variation of the island.", 1, "Height variation");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(nbIsland, seaLevel, heightMultiplier);
    }

    private static final double MIN_FREQ = 0.002;
    private static final double MAX_FREQ = 0.01;

    private static final double MIN_SEA = 16;
    private static final double MAX_SEA = 45;


    @Override
    public void execute(IslandMap map, Context context) {
        double frequency = (MAX_FREQ - MIN_FREQ) * (context.getParamOrDefault(nbIsland)) + MIN_FREQ;
        OpenNoiseMap elevation = new OpenNoiseMap(map.getVertices(), map.getSeed(), map.getSize());

        double intensity = 3.0;
        elevation.addSimplexNoise(intensity, frequency);
        elevation.addSimplexNoise(intensity / 2, frequency / 2);
        elevation.addSimplexNoise(intensity / 4, frequency / 4);

        double sea = (MAX_SEA - MIN_SEA) * (context.getParamOrDefault(seaLevel)) + MIN_SEA;
        elevation.putValuesInRange(sea);
        elevation.multiplyHeights(context.getParamOrDefault(heightMultiplier));
        elevation.putHeightProperty();

        for (Face face : map.getFaces()) {
            if (face.getProperty(faceBorderKey).value && face.getProperty(vertexHeightKey).value > 0) {
                for (Coord coord : face.getBorderVertices()) {
                    coord.putProperty(vertexHeightKey, new DoubleType(0.0));
                }
            }
        }

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