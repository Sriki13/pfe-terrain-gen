package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.SerializableKey;
import pfe.terrain.gen.algo.WaterKind;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.types.BooleanType;

public abstract class WaterGenerator extends Contract {

    private Key<BooleanType> faceWaterKey = new SerializableKey<>(facesPrefix + "IS_WATER", "isWater", BooleanType.class);
    private Key<BooleanType> vertexWaterKey = new SerializableKey<>(verticesPrefix + "IS_WATER", "isWater", BooleanType.class);
    private Key<WaterKind> waterKindKey = new SerializableKey<>(facesPrefix + "WATER_KIND", "waterKind", WaterKind.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asSet(faces, vertices),
                asSet(faceWaterKey, vertexWaterKey, waterKindKey)
        );
    }

}
