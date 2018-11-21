package pfe.terrain.gen;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.algorithms.InitializationGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;

public class BasicInitializer extends InitializationGenerator {

    @Override
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, KeyTypeMismatch {
        map.putProperty(size, context.getPropertyOrDefault(sizeParam, 1024));
        map.putProperty(seed, context.getPropertyOrDefault(seedParam, 0));
    }
}