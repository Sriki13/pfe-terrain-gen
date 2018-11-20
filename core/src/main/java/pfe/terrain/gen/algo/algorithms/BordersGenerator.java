package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BordersGenerator implements Contract {

    public final Key<Boolean> verticeBorderKey = new Key<>("VERTICE_IS_BORDER", Boolean.class);
    public final Key<Boolean> faceBorderKey = new Key<>("FACE_IS_BORDER", Boolean.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                Stream.of(
                        new Key<>("VERTICES", Void.class),
                        new Key<>("EDGES", Void.class),
                        new Key<>("FACES", Void.class)).collect(Collectors.toSet()),
                Stream.of(verticeBorderKey, faceBorderKey).collect(Collectors.toSet())
        );
    }

}
