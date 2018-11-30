package pfe.terrain.gen;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Param;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.Face;

import java.util.HashSet;
import java.util.Set;

public class RiverMoisture extends Contract {

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(faces, edges, AdapterUtils.riverFlowKey),
                asKeySet(AdapterUtils.adaptedMoistureKey),
                asKeySet(AdapterUtils.faceMoisture));
    }

    private final Param<Double> riverMoistureParam = new Param<>("riverMoisture", Double.class, "0-1",
            "The amount of moisture added around the rivers.", 0.5);

    public Set<Param> getRequestedParameters() {
        return asParamSet(riverMoistureParam);
    }

    private static final double MAX_ADD = 0.5;
    private static final double MIN_ADD = 0.1;

    private AdapterUtils utils = new AdapterUtils();

    @Override
    public void execute(IslandMap map, Context context) {
        double moistureBonus = (MAX_ADD - MIN_ADD) * (context.getParamOrDefault(riverMoistureParam)) + MIN_ADD;
        Set<Face> nextToRiver = utils.getTilesNextToRivers(map.getFaces());
        Set<Face> seen = new HashSet<>(nextToRiver);
        for (Face face : nextToRiver) {
            utils.addMoisture(face, moistureBonus);
            utils.spreadToNeighbours(face, seen, moistureBonus / 2);
        }
    }

}
