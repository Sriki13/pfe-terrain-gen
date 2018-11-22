package pfe.terrain.gen.water;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.algorithms.WaterGenerator;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.types.DoubleType;

public abstract class WaterFromHeightGenerator extends WaterGenerator {

    public static final Key<DoubleType> heightKey = new Key<>(verticesPrefix + "HEIGHT", DoubleType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asSet(faces, vertices, heightKey, faceBorderKey, vertexBorderKey),
                asSet(faceWaterKey, vertexWaterKey, waterKindKey)
        );
    }
}
