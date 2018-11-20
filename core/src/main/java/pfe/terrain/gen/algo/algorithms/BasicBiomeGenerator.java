package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.biome.Biome;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BasicBiomeGenerator extends Contract {

    public final Key<Boolean> faceBorderKey = new Key<>(facesPrefix + "IS_BORDER", Boolean.class);
    public final Key<Biome> faceBiomeKey = new Key<>(facesPrefix + "BIOME", Biome.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                Stream.of(faceBorderKey, faces).collect(Collectors.toSet()),
                Stream.of(faceBiomeKey).collect(Collectors.toSet())
        );
    }

}
