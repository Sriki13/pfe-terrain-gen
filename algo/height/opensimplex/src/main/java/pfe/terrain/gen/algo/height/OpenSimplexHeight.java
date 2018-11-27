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

    public static final Param<Double> intensityKey = new Param<>("simplexIntensity", Double.class, "", "", 3.0);
    public static final Param<Double> frequencyKey = new Param<>("simplexFrequency", Double.class, "", "", 0.002);
    public static final Param<Double> seaLevel = new Param<>("simplexSeaLevel", Double.class, "", "", 1.0);
    public static final Param<Double> simplexPower = new Param<>("simplexPower", Double.class, "", "", 1.0);

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(intensityKey, frequencyKey, seaLevel, simplexPower);
    }

    @Override
    public void execute(IslandMap map, Context context)
            throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        double intensity = context.getParamOrDefault(intensityKey);
        double frequency = context.getParamOrDefault(frequencyKey);
        NoiseMap elevation = new NoiseMap(map.getVertices(), map.getSeed(), map.getSize());

        elevation.addSimplexNoise(intensity, frequency);
        elevation.addSimplexNoise(intensity / 2, frequency / 2);
        elevation.addSimplexNoise(intensity / 4, frequency / 4);

        elevation.redistribute(context.getParamOrDefault(simplexPower));
        elevation.putValuesInRange(context.getParamOrDefault(seaLevel));
        elevation.putHeightProperty();

        for (Face face : map.getFaces()) {
            if (face.getProperty(faceBorderKey).value) {
                for (Coord coord : face.getBorderVertices()) {
                    coord.putProperty(vertexHeightKey, new DoubleType(0.0));
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
