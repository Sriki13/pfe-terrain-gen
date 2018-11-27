package pfe.terrain.gen.algo.gridcreator;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.Param;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.CoordSet;

import java.util.Random;
import java.util.Set;

public class RandomPoints extends Contract {

    private Param<Integer> nbPoints = new Param<>("nbPoints", Integer.class,
            "100-100000", "number of points in the map (=tiles)", 1024);

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(nbPoints);
    }

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(size, seed), asKeySet(new Key<>("POINTS", CoordSet.class)));
    }

    @Override
    public void execute(IslandMap islandMap, Context context) throws DuplicateKeyException, KeyTypeMismatch {
        int numberOfPoints = context.getParamOrDefault(nbPoints);
        CoordSet points = new CoordSet();
        Random random = new Random(islandMap.getSeed());
        for (int i = 0; i < numberOfPoints; i++) {
            points.add(new Coord(random.nextDouble() * islandMap.getSize(), random.nextDouble() * islandMap.getSize()));
        }
        islandMap.putProperty(new Key<>("POINTS", CoordSet.class), points);
    }

}
