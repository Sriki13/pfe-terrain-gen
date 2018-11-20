package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BordersGenerator extends Contract {

    public final Key<Boolean> verticeBorderKey = new Key<>(verticesPrefix + "IS_BORDER", Boolean.class);
    public final Key<Boolean> faceBorderKey = new Key<>(facesPrefix + "IS_BORDER", Boolean.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                Stream.of(vertices, edges, faces).collect(Collectors.toSet()),
                Stream.of(verticeBorderKey, faceBorderKey).collect(Collectors.toSet())
        );
    }
}
