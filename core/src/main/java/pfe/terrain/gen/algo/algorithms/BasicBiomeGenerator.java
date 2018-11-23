package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Biome;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.SerializableKey;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.types.BooleanType;

public abstract class BasicBiomeGenerator extends Contract {

    public final Key<BooleanType> faceBorderKey =
            new Key<>(facesPrefix + "IS_BORDER", BooleanType.class);
    public final Key<Biome> faceBiomeKey =
            new SerializableKey<>(facesPrefix + "BIOME", "biome", Biome.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asSet(faceBorderKey, faces),
                asSet(faceBiomeKey)
        );
    }

}
