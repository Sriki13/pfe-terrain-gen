package pfe.terrain.gen.algo.gridcreator;

import com.vividsolutions.jts.geom.Coordinate;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.algorithms.PointsGenerator;
import pfe.terrain.gen.algo.geometry.CoordSet;

import java.util.Random;

public class RandomPoints implements PointsGenerator {
    @Override
    public void generatePoint(IslandMap islandMap, int numberOfPoints) throws DuplicateKeyException {
        CoordSet points = new CoordSet();
        Random random = new Random();
        for (int i = 0; i < numberOfPoints; i++) {
            points.add(new Coordinate(random.nextDouble() * islandMap.getSize(), random.nextDouble() * islandMap.getSize()));
        }
        islandMap.putProperty(new Key<>("POINTS",CoordSet.class),points);
    }
}
