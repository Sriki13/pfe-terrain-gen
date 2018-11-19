package pfe.terrain.gen.algo.gridcreator;

import com.vividsolutions.jts.geom.Coordinate;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.algorithms.PointsGenerator;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.CoordSet;

public class GridPoints implements PointsGenerator {

    @Override
    public void execute(IslandMap islandMap) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        int numberOfPoints = this.getDefaultNbPoint();
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
        islandMap.putProperty(new Key<>("POINTS",CoordSet.class), points);
    }

    @Override
    public String getName() {
        return "GridPoints";
    }
}
