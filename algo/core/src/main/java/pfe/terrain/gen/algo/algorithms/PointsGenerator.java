package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.DuplicateKeyException;
import pfe.terrain.gen.algo.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface PointsGenerator extends Contract {
    void generatePoint(IslandMap islandMap, int numberOfPoints) throws InvalidAlgorithmParameters, DuplicateKeyException;

    @Override
    default Constraints getContract() {
        return new Constraints(new HashSet<>(), Stream.of("POINTS").collect(Collectors.toSet()));
    }
}
