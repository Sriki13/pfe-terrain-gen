package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.types.DoubleType;

public abstract class WaterFromAltitudeGenerator extends WaterGenerator {

    @Override
    public Constraints getContract() {
        return new Constraints(
                asSet(faces, vertices, new Key<>(verticesPrefix + "HEIGHT", DoubleType.class)),
                asSet(faceWaterKey, vertexWaterKey, waterKindKey)
        );
    }
}
