package pfe.terrain.gen.algo.gridcreator;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.CoordSet;

import java.util.Random;
import java.util.Set;

public class RandomPoints extends Contract {

    private Param<Integer> NB_POINTS = new Param<>("nbPoints", Integer.class, 64, 100000,
            "Number of points in the map (=tiles)", 1024, "Number of points");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(NB_POINTS);
    }

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(SIZE, SEED), asKeySet(new Key<>("POINTS", CoordSet.class)));
    }

    @Override
    public void execute(IslandMap islandMap, Context context) {
        int numberOfPoints = context.getParamOrDefault(NB_POINTS);
        CoordSet points = new CoordSet();
        Random random = new Random(islandMap.getSeed());
        for (int i = 0; i < numberOfPoints; i++) {
            points.add(new Coord(random.nextDouble() * islandMap.getSize(), random.nextDouble() * islandMap.getSize()));
        }
        islandMap.putProperty(new Key<>("POINTS", CoordSet.class), points);
    }

}
