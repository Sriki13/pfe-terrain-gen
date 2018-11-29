package pfe.terrain.gen;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Param;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;

import java.util.Collections;
import java.util.Set;

public class BasicInitializer extends Contract {

    private Param<Integer> sizeParam = new Param<>("size", Integer.class, "100-10000", "size of the island in a visualization sense", 400);
    private Param<Integer> seedParam = new Param<>("seed", Integer.class, "0-4000000000", "seed of the map, defines the behaviour of the random functions", 0);

    @Override
    public Constraints getContract() {
        return new Constraints(Collections.emptySet(),
                asKeySet(seed, size));
    }

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(seedParam, sizeParam);
    }

    @Override
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, KeyTypeMismatch {
        map.putProperty(size, context.getParamOrDefault(sizeParam));
        map.putProperty(seed, context.getParamOrDefault(seedParam));
    }
}