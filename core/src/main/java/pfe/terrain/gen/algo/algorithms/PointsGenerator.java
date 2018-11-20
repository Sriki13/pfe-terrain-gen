package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.CoordSet;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PointsGenerator extends Contract {

    protected int getDefaultNbPoint() {
        return 1024;
    }

    @Override
    public Constraints getContract() {
        return new Constraints(new HashSet<>(), Stream.of(new Key<>("POINTS", CoordSet.class)).collect(Collectors.toSet()));
    }
}
