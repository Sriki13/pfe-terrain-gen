package pfe.terrain.gen.algo.gridcreator;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.algorithms.PointsGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.CoordSet;

import java.util.Random;

public class RandomPoints extends PointsGenerator {


    @Override
    public void execute(IslandMap islandMap, Context context) throws DuplicateKeyException, KeyTypeMismatch {
        int numberOfPoints = context.getPropertyOrDefault(nbPoints, getDefaultNbPoint());
        CoordSet points = new CoordSet();
        Random random = new Random(islandMap.getSeed());
        for (int i = 0; i < numberOfPoints; i++) {
            points.add(new Coord(random.nextDouble() * islandMap.getSize(), random.nextDouble() * islandMap.getSize()));
        }
        islandMap.putProperty(new Key<>("POINTS",CoordSet.class),points);
    }

}
