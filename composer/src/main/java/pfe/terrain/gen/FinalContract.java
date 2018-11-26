package pfe.terrain.gen;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.EdgeSet;
import pfe.terrain.gen.algo.geometry.FaceSet;

import java.util.HashSet;
import java.util.Set;

public class FinalContract extends Contract {

    public static final String FINAL_CONTRACT_NAME = "Final";

    @Override
    public Constraints getContract() {
        Set<Key> required = asSet(
                new Key<>("VERTICES", CoordSet.class),
                new Key<>("EDGES", EdgeSet.class),
                new Key<>("FACES", FaceSet.class));

        return new Constraints(required, new HashSet<>());

    }

    @Override
    public void execute(IslandMap map, Context context) {

    }

    @Override
    public String getName() {
        return FINAL_CONTRACT_NAME;
    }


}
