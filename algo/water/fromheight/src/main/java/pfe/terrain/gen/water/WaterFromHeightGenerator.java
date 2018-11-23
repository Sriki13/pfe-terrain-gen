package pfe.terrain.gen.water;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.SerializableKey;
import pfe.terrain.gen.algo.WaterKind;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

public abstract class WaterFromHeightGenerator extends Contract {

    public static final Key<DoubleType> heightKey =
            new Key<>(verticesPrefix + "HEIGHT", DoubleType.class);

    public static final Key<BooleanType> vertexBorderKey =
            new Key<>(verticesPrefix + "IS_BORDER", BooleanType.class);

    public static final Key<BooleanType> faceBorderKey =
            new Key<>(facesPrefix + "IS_BORDER", BooleanType.class);
    public static final Key<BooleanType> faceWaterKey =
            new SerializableKey<>(facesPrefix + "IS_WATER", "isWater", BooleanType.class);

    public static final Key<BooleanType> vertexWaterKey =
            new SerializableKey<>(verticesPrefix + "IS_WATER", "isWater", BooleanType.class);
    public static final Key<WaterKind> waterKindKey =
            new SerializableKey<>(facesPrefix + "WATER_KIND", "waterKind", WaterKind.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asSet(faces, vertices, heightKey, faceBorderKey, vertexBorderKey),
                asSet(faceWaterKey, vertexWaterKey, waterKindKey)
        );
    }
}
