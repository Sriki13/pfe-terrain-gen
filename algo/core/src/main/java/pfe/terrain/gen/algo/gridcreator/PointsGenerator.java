package pfe.terrain.gen.algo.gridcreator;

import pfe.terrain.gen.algo.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.IslandMap;

public interface PointsGenerator {

    void generatePoint(IslandMap islandMap, int numberOfPoints) throws InvalidAlgorithmParameters;

}
