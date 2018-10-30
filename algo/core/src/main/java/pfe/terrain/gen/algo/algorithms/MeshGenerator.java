package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.DuplicateKeyException;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.KeyTypeMismatch;
import pfe.terrain.gen.algo.NoSuchKeyException;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface MeshGenerator extends Contract {

    void generateMesh(IslandMap map) throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch;

    @Override
    default Constraints getContract() {
        return new Constraints(Stream.of("POINTS").collect(Collectors.toSet()),
                Stream.of("VERTICES", "EDGES", "FACES").collect(Collectors.toSet()));
    }

}
