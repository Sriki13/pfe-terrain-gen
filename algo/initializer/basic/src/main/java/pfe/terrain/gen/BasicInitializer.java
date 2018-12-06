package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.TerrainMap;

import java.util.Collections;
import java.util.Set;

public class BasicInitializer extends Contract {

    private Param<Integer> SIZE_PARAM = new Param<>("size", Integer.class, 100, 10000,
            "Size of the island in a visualization sense", 400, "Size of the island");

    private Param<Integer> SEED_PARAM = Param.generatePositiveIntegerParam("seed", Integer.MAX_VALUE,
            "Seed of the map, defines the behaviour of the random functions", 0, "Island seed");

    @Override
    public Constraints getContract() {
        return new Constraints(Collections.emptySet(),
                asKeySet(SEED, SIZE));
    }

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(SEED_PARAM, SIZE_PARAM);
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        map.putProperty(SIZE, context.getParamOrDefault(SIZE_PARAM));
        map.putProperty(SEED, context.getParamOrDefault(SEED_PARAM));
    }

}