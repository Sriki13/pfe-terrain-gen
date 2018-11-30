package pfe.terrain.gen;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.Param;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.Face;

import java.util.HashSet;
import java.util.Set;

public class LakeMoisture extends Contract {

    public static final Key<Void> lakesKey = new Key<>("LAKES", Void.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(faces, edges, lakesKey, AdapterUtils.faceWaterKey, AdapterUtils.waterKindKey),
                asKeySet(AdapterUtils.adaptedMoistureKey),
                asKeySet(AdapterUtils.faceMoisture));
    }

    private final Param<Double> lakeMoistureParam = new Param<>("lakeMoisture", Double.class, "0-1",
            "The amount of moisture added around the lakes.", 0.5, "Lake extra moisture");

    public Set<Param> getRequestedParameters() {
        return asParamSet(lakeMoistureParam);
    }

    private static final double MAX_ADD = 0.5;
    private static final double MIN_ADD = 0.1;

    private AdapterUtils utils = new AdapterUtils();

    @Override
    public void execute(IslandMap map, Context context) {
        double moistureBonus = (MAX_ADD - MIN_ADD) * (context.getParamOrDefault(lakeMoistureParam)) + MIN_ADD;
        Set<Face> nextToLake = utils.getTilesNextToLakes(map.getFaces());
        Set<Face> seen = new HashSet<>(nextToLake);
        for (Face face : nextToLake) {
            utils.addMoisture(face, moistureBonus);
            utils.spreadToNeighbours(face, seen, moistureBonus / 2);
        }
    }

}
