package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.CoordSet;

import java.util.Set;

public abstract class PointsGenerator extends Contract {

    protected int getDefaultNbPoint() {
        return 64;
    }

    protected Key<Integer> nbPoints = new Key<>("nbPoints", Integer.class);

    @Override
    public Set<Key> getRequestedParameters() {
        return asSet(nbPoints);
    }

    @Override
    public Constraints getContract() {
        return new Constraints(asSet(size, seed), asSet(new Key<>("POINTS", CoordSet.class)));
    }
}
