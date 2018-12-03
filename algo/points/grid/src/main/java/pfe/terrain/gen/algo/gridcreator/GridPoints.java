package pfe.terrain.gen.algo.gridcreator;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.key.Key;
import pfe.terrain.gen.algo.key.Param;

import java.util.Set;

public class GridPoints extends Contract {


    private Param<Integer> nbPoints = new Param<>("nbPoints", Integer.class, 64, 65536,
            "Number of points in the map (=tiles)", 1024, "Number of points (power of 2)");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(nbPoints);
    }

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(size, seed), asKeySet(new Key<>("POINTS", CoordSet.class)));
    }

    @Override
    public void execute(IslandMap islandMap, Context context) {
        int numberOfPoints = context.getParamOrDefault(nbPoints);
        double pointsByLineDouble = Math.sqrt(numberOfPoints);
        if (!(pointsByLineDouble - Math.floor(pointsByLineDouble) == 0)) {
            throw new InvalidAlgorithmParameters("numberOfPoints must be a square root");
        }
        double squareSide = islandMap.getSize() / pointsByLineDouble;
        double halfSide = squareSide / 2;
        CoordSet points = new CoordSet();

        for (int x = 0; x < pointsByLineDouble; x++) {
            for (int y = 0; y < pointsByLineDouble; y++) {
                points.add(new Coord(x * squareSide + halfSide, y * squareSide + halfSide));
            }
        }
        islandMap.putProperty(new Key<>("POINTS", CoordSet.class), points);
    }
}
