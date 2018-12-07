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

import java.util.Set;

public class OpenSimplexHeight extends Contract {

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

    public static final Param<Double> nbIsland = Param.generateDefaultDoubleParam("nbIsland",
            "The amount of islands that will be generated. Higher values mean the map will be an archipelago.", 0.0, "Number of islands");

    public static final Param<Double> seaLevel = Param.generateDefaultDoubleParam("seaLevel",
            "The height of the sea level. Higher values mean less land will emerge.", 0.55, "Sea level");

    public static final Param<Integer> heightMultiplier = Param.generatePositiveIntegerParam("heightMultiplier", 100,
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
    public void execute(TerrainMap map, Context context) {
        double frequency = (MAX_FREQ - MIN_FREQ) * (context.getParamOrDefault(nbIsland)) + MIN_FREQ;
        OpenNoiseMap elevation = new OpenNoiseMap(map.getProperty(VERTICES), map.getProperty(SEED), map.getProperty(SIZE));

        double intensity = 3.0;
        elevation.addSimplexNoise(intensity, frequency);
        elevation.addSimplexNoise(intensity / 2, frequency / 2);
        elevation.addSimplexNoise(intensity / 4, frequency / 4);

        double sea = (MAX_SEA - MIN_SEA) * (context.getParamOrDefault(seaLevel)) + MIN_SEA;
        elevation.putValuesInRange(sea);
        elevation.multiplyHeights(context.getParamOrDefault(heightMultiplier));
        elevation.putHeightProperty();

        for (Face face : map.getProperty(FACES)) {
            if (face.hasProperty(FACE_BORDER_KEY)) {
                for (Coord coord : face.getBorderVertices()) {
                    if (coord.getProperty(VERTEX_HEIGHT_KEY).value > 0) {
                        coord.putProperty(VERTEX_HEIGHT_KEY, new DoubleType(0.0));
                    }
                }
            }
        }

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