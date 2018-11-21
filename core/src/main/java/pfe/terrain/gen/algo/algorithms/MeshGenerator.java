package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.CoordSet;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MeshGenerator extends Contract {

    @Override
    public Constraints getContract() {
        return new Constraints(
                Stream.of(new Key<>("POINTS", CoordSet.class), size).collect(Collectors.toSet()),
                Stream.of(vertices, edges, faces).collect(Collectors.toSet()));
    }

}
