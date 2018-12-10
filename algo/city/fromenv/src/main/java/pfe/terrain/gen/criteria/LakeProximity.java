package pfe.terrain.gen.criteria;

import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.geometry.Face;

import java.util.Map;
import java.util.Set;

public class LakeProximity implements Criterion {

    public static final Param<Double> CITY_LAKE_WEIGHT = Param.generateDefaultDoubleParam(
            "cityLakeHeight", "The multiplier for the bonus a location gets from being near a lake", 0.1, "Lake proximity bonus"
    );

    private Set<Face> lakes;

    public LakeProximity(Set<Face> lakes) {
        this.lakes = lakes;
    }

    @Override
    public void assignScores(Context context, Map<Face, Double> scores) {
        double weight = context.getParamOrDefault(CITY_LAKE_WEIGHT) * 100;
        lakes.forEach(lake ->
                scores.forEach((key, value) ->
                        scores.put(key, value + (1 / (lake.getCenter().distance(key.getCenter()) + 0.1)) * weight)));
    }

}
