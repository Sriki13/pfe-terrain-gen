package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.HashSet;
import java.util.Set;

public class RiverLakeMoisture extends Contract {

    public static final Key<MarkerType> lakesKey = new Key<>("LAKES", MarkerType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, EDGES, AdapterUtils.RIVER_FLOW_KEY, lakesKey),
                asKeySet(AdapterUtils.ADAPTED_MOISTURE_KEY),
                asKeySet(AdapterUtils.FACE_MOISTURE));
    }

    public static final Param<Double> moistureParam = Param.generateDefaultDoubleParam("riverMoisture",
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
