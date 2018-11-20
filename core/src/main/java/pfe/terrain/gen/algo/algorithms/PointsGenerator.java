package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.CoordSet;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PointsGenerator extends Contract {

    protected int getDefaultNbPoint() {
        return 1024;
    }

    protected Key<Integer> nbPoints = new Key<>("nbPoints", Integer.class);

    @Override
    public Set<Key> getRequestedParameters() {
        return Stream.of(nbPoints).collect(Collectors.toSet());
    }

    @Override
    public Constraints getContract() {
        return new Constraints(Stream.of(size, seed).collect(Collectors.toSet())
                , Stream.of(new Key<>("POINTS", CoordSet.class)).collect(Collectors.toSet()));
    }
}
