package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Biome;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.SerializableKey;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BasicBiomeGenerator extends Contract {

    public final Key<BooleanType> faceBorderKey =
            new SerializableKey<>(facesPrefix + "IS_BORDER", "is_border", BooleanType.class);
    public final Key<Biome> faceBiomeKey =
            new SerializableKey<>(facesPrefix + "BIOME", "biome", Biome.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                Stream.of(faceBorderKey, faces).collect(Collectors.toSet()),
                Stream.of(faceBiomeKey).collect(Collectors.toSet())
        );
    }

}
