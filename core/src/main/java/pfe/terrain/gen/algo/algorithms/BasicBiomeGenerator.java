package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.biome.Biome;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.FaceSet;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BasicBiomeGenerator implements Contract {

    public final Key<Boolean> verticeBorderKey = new Key<>("VERTICE_IS_BORDER", Boolean.class);
    public final Key<Boolean> faceBorderKey = new Key<>("FACE_IS_BORDER", Boolean.class);
    public final Key<Biome> faceBiomeKey = new Key<>("FACE_BIOME", Biome.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                Stream.of(
                        verticeBorderKey, faceBorderKey, new Key<>("FACES", FaceSet.class))
                        .collect(Collectors.toSet()),
                Stream.of(faceBiomeKey).collect(Collectors.toSet())
        );
    }

}
