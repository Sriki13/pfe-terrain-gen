package pfe.terrain.gen;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;

import java.util.Collections;
import java.util.Set;

public class BasicInitializer extends Contract {

    private Key<Integer> sizeParam = new Key<>("size", Integer.class);
    private Key<Integer> seedParam = new Key<>("seed", Integer.class);

    @Override
    public Constraints getContract() {
        return new Constraints(Collections.emptySet(),
                asSet(seed, size));
    }

    @Override
    public Set<Key> getRequestedParameters() {
        return asSet(seedParam, sizeParam);
    }

    @Override
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, KeyTypeMismatch {
        map.putProperty(size, context.getPropertyOrDefault(sizeParam, 1024));
        map.putProperty(seed, context.getPropertyOrDefault(seedParam, 0));
    }
}