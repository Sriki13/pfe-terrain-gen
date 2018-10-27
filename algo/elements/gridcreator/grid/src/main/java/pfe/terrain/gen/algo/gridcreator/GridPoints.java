package pfe.terrain.gen.algo.gridcreator;

import com.vividsolutions.jts.geom.Coordinate;
import pfe.terrain.gen.algo.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Property;
import pfe.terrain.gen.algo.algorithms.PointsGenerator;

import java.util.HashSet;
import java.util.Set;

public class GridPoints implements PointsGenerator {

    @Override
    public void generatePoint(IslandMap islandMap, int numberOfPoints) throws InvalidAlgorithmParameters {
        double pointsByLineDouble = Math.sqrt(numberOfPoints);
        if (!(pointsByLineDouble - Math.floor(pointsByLineDouble) == 0)) {
            throw new InvalidAlgorithmParameters("numberOfPoints must be a square root");
        }
        double squareSide = islandMap.getSize() / pointsByLineDouble;
        double halfSide = squareSide / 2;
        Set<Coordinate> points = new HashSet<>();

        for (int x = 0; x < pointsByLineDouble; x++) {
            for (int y = 0; y < pointsByLineDouble; y++) {
                points.add(new Coordinate(x * squareSide + halfSide, y * squareSide + halfSide));
            }
        }
        islandMap.putProperty(Property.POINTS, points, Set.class);
    }
}
