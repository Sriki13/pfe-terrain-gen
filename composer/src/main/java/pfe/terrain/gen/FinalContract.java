package pfe.terrain.gen;

import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.EdgeSet;
import pfe.terrain.gen.algo.geometry.FaceSet;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FinalContract implements Contract {

    @Override
    public Constraints getContract() {
        Set<Key> required = Stream.of(
                new Key<>("VERTICES", CoordSet.class),
                new Key<>("EDGES", EdgeSet.class),
                new Key<>("FACES", FaceSet.class)).collect(Collectors.toSet());

        return new Constraints(required,new HashSet<>());

    }

    @Override
    public void execute(IslandMap map) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

    }

    @Override
    public String getName() {
        return "Final";
    }


}
