package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.island.geometry.EdgeSet;
import pfe.terrain.gen.algo.island.geometry.FaceSet;

import java.util.HashSet;
import java.util.Set;

public class FinalContract extends Contract {

    public static final String FINAL_CONTRACT_NAME = "Final";

    @Override
    public Constraints getContract() {
        Set<Key> required = asKeySet(
                new Key<>("VERTICES", CoordSet.class),
                new Key<>("EDGES", EdgeSet.class),
                new Key<>("FACES", FaceSet.class));

        return new Constraints(required, new HashSet<>());

    }

    @Override
    public String getDescription() {
        return "Contains minimum elements to create a map";
    }

    @Override
    public void execute(TerrainMap map, Context context) {

    }

    @Override
    public String getName() {
        return FINAL_CONTRACT_NAME;
    }


}