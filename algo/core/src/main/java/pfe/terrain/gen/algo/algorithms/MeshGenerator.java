package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Property;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface MeshGenerator extends Contract {

    void generateMesh(IslandMap map);

    @Override
    default Constraints getContract() {
        return new Constraints(Stream.of(Property.POINTS).collect(Collectors.toSet()),
                Stream.of(Property.VERTICES, Property.EDGES, Property.FACES).collect(Collectors.toSet()));
    }

}
