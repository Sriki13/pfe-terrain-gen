package pfe.terrain.gen;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Param;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.Face;

import java.util.HashSet;
import java.util.Set;

public class RiverLakeMoisture extends Contract {

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(faces, edges, AdapterUtils.riverFlowKey),
                asKeySet(AdapterUtils.adaptedMoistureKey),
                asKeySet(AdapterUtils.faceMoisture));
    }

    public static final Param<Double> moistureParam = new Param<>("riverMoisture", Double.class, "0-1",
            "The amount of moisture added around the rivers and lakes.", 0.5, "River and lake extra moisture");


    public Set<Param> getRequestedParameters() {
        return asParamSet(moistureParam);
    }

    private static final double MAX_ADD = 0.5;
    private static final double MIN_ADD = 0.1;

    private AdapterUtils utils = new AdapterUtils();

    @Override
    public void execute(IslandMap map, Context context) {
        double bonus = (MAX_ADD - MIN_ADD) * (context.getParamOrDefault(moistureParam)) + MIN_ADD;
        Set<Face> nextToRiver = utils.getTilesNextToRivers(map.getFaces());
        Set<Face> seen = new HashSet<>(nextToRiver);
        utils.setModifiedKey(map.getFaces());
        for (Face face : nextToRiver) {
            utils.addMoisture(face, bonus);
            utils.spreadToNeighbours(face, seen, bonus / 2);
        }
        Set<Face> nextToLake = utils.getTilesNextToLakes(map.getFaces());
        for (Face face : nextToLake) {
            if (!seen.contains(face)) {
                utils.addMoisture(face, bonus);
                utils.spreadToNeighbours(face, seen, bonus / 2);
            }
        }
    }

}
