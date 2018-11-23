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
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Set;

public class OpenSimplexHeight extends Contract {

    public static final Key<BooleanType> verticeBorderKey =
            new Key<>(verticesPrefix + "IS_BORDER", BooleanType.class);
    public static final Key<BooleanType> faceBorderKey =
            new Key<>(facesPrefix + "IS_BORDER", BooleanType.class);

    public static final Key<DoubleType> vertexHeightKey =
            new SerializableKey<>(verticesPrefix + "HEIGHT", "height", DoubleType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asSet(faces, vertices, verticeBorderKey, faceBorderKey),
                asSet(vertexHeightKey)
        );
    }

    private static final Key<Double> intensityKey = new Key<>("simplexIntensity", Double.class);
    private static final Key<Double> frequencyKey = new Key<>("simplexFrequency", Double.class);
    private static final Key<Double> seaLevel = new Key<>("simplexSeaLevel", Double.class);

    @Override
    public Set<Key> getRequestedParameters() {
        return asSet(intensityKey, frequencyKey, seaLevel);
    }

    @Override
    public void execute(IslandMap map, Context context)
            throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        double intensity = context.getPropertyOrDefault(intensityKey, 0.7);
        double frequency = context.getPropertyOrDefault(frequencyKey, 0.05);
        NoiseMap elevation = new NoiseMap(map.getVertices(), map.getSeed());

        elevation.addSimplexNoise(intensity, frequency);
        elevation.addSimplexNoise(intensity / 2, frequency / 2);
        elevation.addSimplexNoise(intensity / 4, frequency / 4);

        elevation.redistribute(3);
        elevation.putValuesInRange(context.getPropertyOrDefault(seaLevel, 16.0));
        elevation.ensureBordersAreLow();
        elevation.putHeightProperty();

    }
}
