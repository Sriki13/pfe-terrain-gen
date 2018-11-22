package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.SerializableKey;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.types.BooleanType;

public abstract class BordersGenerator extends Contract {

    public final Key<BooleanType> verticeBorderKey =
            new SerializableKey<>(verticesPrefix + "IS_BORDER", "isBorder", BooleanType.class);
    public final Key<BooleanType> faceBorderKey =
            new SerializableKey<>(facesPrefix + "IS_BORDER", "isBorder", BooleanType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asSet(vertices, edges, faces),
                asSet(verticeBorderKey, faceBorderKey)
        );
    }

}
