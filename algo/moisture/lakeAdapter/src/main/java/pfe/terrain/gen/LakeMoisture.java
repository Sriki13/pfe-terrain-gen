package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.HashSet;
import java.util.Set;

public class LakeMoisture extends Contract {

    public static final Key<MarkerType> LAKES_KEY = new Key<>("LAKES", MarkerType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, EDGES, LAKES_KEY, AdapterUtils.FACE_WATER_KEY, AdapterUtils.WATER_KIND_KEY),
                asKeySet(AdapterUtils.ADAPTED_MOISTURE_KEY),
                asKeySet(AdapterUtils.FACE_MOISTURE));
    }

    private final Param<Double> LAKE_MOISTURE_PARAM = Param.generateDefaultDoubleParam("lakeMoisture",
            "The amount of moisture added around the lakes.", 0.5, "Lake extra moisture");

    public Set<Param> getRequestedParameters() {
        return asParamSet(LAKE_MOISTURE_PARAM);
    }

    private static final double MAX_ADD = 0.5;
    private static final double MIN_ADD = 0.1;

    private AdapterUtils utils = new AdapterUtils();

    @Override
    public void execute(TerrainMap map, Context context) {
        double moistureBonus = (MAX_ADD - MIN_ADD) * (context.getParamOrDefault(LAKE_MOISTURE_PARAM)) + MIN_ADD;
        Set<Face> nextToLake = utils.getTilesNextToLakes(map.getProperty(FACES));
        Set<Face> seen = new HashSet<>(nextToLake);
        for (Face face : nextToLake) {
            utils.addMoisture(face, moistureBonus);
            utils.spreadToNeighbours(face, seen, moistureBonus / 2);
        }
    }

}
