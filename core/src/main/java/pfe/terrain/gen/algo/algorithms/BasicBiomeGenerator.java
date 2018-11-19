package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.biome.BiomeMap;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.BordersSet;
import pfe.terrain.gen.algo.geometry.FaceSet;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface BasicBiomeGenerator extends Contract {

    @Override
    default Constraints getContract() {
        return new Constraints(
                Stream.of(
                        new Key<>("BORDERS", BordersSet.class),
                        new Key<>("FACES", FaceSet.class))
                        .collect(Collectors.toSet()),
                Stream.of(
                        new Key<>("BIOMES", BiomeMap.class))
                        .collect(Collectors.toSet())
        );
    }

}
