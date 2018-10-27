package pfe.terrain.gen.algo.gridcreator;

import com.vividsolutions.jts.geom.Coordinate;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Property;
import pfe.terrain.gen.algo.algorithms.PointsGenerator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomPoints implements PointsGenerator {
    @Override
    public void generatePoint(IslandMap islandMap, int numberOfPoints) {
        Set<Coordinate> points = new HashSet<>();
        Random random = new Random();
        for (int i = 0; i < numberOfPoints; i++) {
            points.add(new Coordinate(random.nextDouble() * islandMap.getSize(), random.nextDouble() * islandMap.getSize()));
        }
        islandMap.putProperty(Property.POINTS, points, Set.class);
    }
}
