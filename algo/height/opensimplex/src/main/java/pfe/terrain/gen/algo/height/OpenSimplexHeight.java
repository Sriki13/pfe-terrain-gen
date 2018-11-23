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

    @Override
    public Set<Key> getRequestedParameters() {
        return null;
    }

    @Override
    public void execute(IslandMap map, Context context)
            throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        NoiseMap elevation = new NoiseMap(map.getVertices(), map.getSeed());

        elevation.addSimplexNoise(0.7, 0.05);
        elevation.addSimplexNoise(0.35, 0.025);
        elevation.addSimplexNoise(0.175, 0.0125);

        elevation.redistribute(3);
        elevation.putValuesInRange();
        elevation.ensureBordersAreLow();
        elevation.putHeightProperty();

    }
}
