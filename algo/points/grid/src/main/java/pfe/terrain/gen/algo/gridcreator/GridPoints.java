package pfe.terrain.gen.algo.gridcreator;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.CoordSet;

import java.util.Set;

public class GridPoints extends Contract {

    private Param<Integer> NB_POINTS = new Param<>("nbPoints", Integer.class, 64, 65536,
            "Number of points in the map (=tiles)", 1024, "Number of points (power of 2)");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(NB_POINTS);
    }

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(SIZE, SEED), asKeySet(new Key<>("POINTS", CoordSet.class)));
    }

    @Override
    public String getDescription() {
        return "Adds points to the map to generate mesh, these points are arranged in a square grid," +
                "as such, number of points has to be a square";
    }

    @Override
    public void execute(TerrainMap terrainMap, Context context) {
        int numberOfPoints = context.getParamOrDefault(NB_POINTS);
        double pointsByLineDouble = Math.sqrt(numberOfPoints);
        if (!(pointsByLineDouble - Math.floor(pointsByLineDouble) == 0)) {
            throw new InvalidAlgorithmParameters("numberOfPoints must be a square root");
        }
        double squareSide = terrainMap.getProperty(SIZE) / pointsByLineDouble;
        double halfSide = squareSide / 2;
        CoordSet points = new CoordSet();

        for (int x = 0; x < pointsByLineDouble; x++) {
            for (int y = 0; y < pointsByLineDouble; y++) {
                points.add(new Coord(x * squareSide + halfSide, y * squareSide + halfSide));
            }
        }
        terrainMap.putProperty(new Key<>("POINTS", CoordSet.class), points);
    }
}
