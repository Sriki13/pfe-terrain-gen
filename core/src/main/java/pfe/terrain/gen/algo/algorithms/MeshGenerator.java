package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.CoordSet;

public abstract class MeshGenerator extends Contract {

    @Override
    public Constraints getContract() {
        return new Constraints(
                asSet(new Key<>("POINTS", CoordSet.class), size),
                asSet(vertices, edges, faces)
        );
    }
}
