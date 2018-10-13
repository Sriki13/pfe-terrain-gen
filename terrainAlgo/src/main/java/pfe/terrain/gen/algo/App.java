package pfe.terrain.gen.algo;

import pfe.terrain.gen.algo.gridcreator.PointsGenerator;
import pfe.terrain.gen.algo.gridcreator.RelaxedPoints;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        IslandMap islandMap = new IslandMap();
        islandMap.setSize(8);
        PointsGenerator g = new RelaxedPoints();
        try {
            g.generatePoint(islandMap, 4);
            System.out.println(islandMap.getPoints());
        } catch (InvalidAlgorithmParameters invalidAlgorithmParameters) {
            invalidAlgorithmParameters.printStackTrace();
        }
    }
}
