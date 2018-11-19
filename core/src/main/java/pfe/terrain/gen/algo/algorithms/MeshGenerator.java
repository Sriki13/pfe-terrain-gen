package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.EdgeSet;
import pfe.terrain.gen.algo.geometry.FaceSet;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface MeshGenerator extends Contract {


    @Override
    default Constraints getContract() {
        return new Constraints(Stream.of(new Key<>("POINTS", CoordSet.class)).collect(Collectors.toSet()),
                Stream.of(
                        new Key<>("VERTICES", CoordSet.class),
                        new Key<>("EDGES", EdgeSet.class),
                        new Key<>("FACES", FaceSet.class)).collect(Collectors.toSet()));
    }

}
