package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.SerializableKey;
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
                asSet(faces, vertices, vertexBorderKey, faceBorderKey, size, seed),
                asSet(vertexHeightKey)
        );
    }

    public static final Key<Double> intensityKey = new Key<>("simplexIntensity", Double.class);
    public static final Key<Double> frequencyKey = new Key<>("simplexFrequency", Double.class);
    public static final Key<Double> seaLevel = new Key<>("simplexSeaLevel", Double.class);
    public static final Key<Double> simplexPower = new Key<>("simplexPower", Double.class);
    public static final Key<Boolean> fixCliffs = new Key<>("simplexFixCliffs", Boolean.class);

    @Override
    public Set<Key> getRequestedParameters() {
        return asSet(intensityKey, frequencyKey, seaLevel, simplexPower, fixCliffs);
    }

    @Override
    public void execute(IslandMap map, Context context)
            throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        double intensity = context.getPropertyOrDefault(intensityKey, 3.0);
        double frequency = context.getPropertyOrDefault(frequencyKey, 0.002);
        NoiseMap elevation = new NoiseMap(map.getVertices(), map.getSeed());

        elevation.addSimplexNoise(intensity, frequency);
        elevation.addSimplexNoise(intensity / 2, frequency / 2);
        elevation.addSimplexNoise(intensity / 4, frequency / 4);

        elevation.redistribute(context.getPropertyOrDefault(simplexPower, 1.0));
        elevation.putValuesInRange(context.getPropertyOrDefault(seaLevel, 32.0), map.getSize());
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
