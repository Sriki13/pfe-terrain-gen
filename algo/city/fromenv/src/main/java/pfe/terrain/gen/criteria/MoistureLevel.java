package pfe.terrain.gen.criteria;

import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Map;

import static pfe.terrain.gen.algo.constraints.Contract.FACES_PREFIX;

public class MoistureLevel implements Criterion {

    public static final Key<DoubleType> MOISTURE_KEY =
            new Key<>(FACES_PREFIX + "MOISTURE", DoubleType.class);

    public static final double IDEAL = 0.5;

    public static final Param<Double> CITY_MOISTURE_WEIGHT = Param.generateDefaultDoubleParam(
            "cityMoistureWeight", "The multiplier applied to the bonus a location gets from being close to 50% moisture",
            0.1, "Moisture bonus"
    );

    @Override
    public void assignScores(Context context, Map<Face, Double> scores) {
        double weight = context.getParamOrDefault(CITY_MOISTURE_WEIGHT) * 10;
        scores.forEach((key, value) ->
                scores.put(key, value + (1 / (Math.abs(IDEAL - key.getProperty(MOISTURE_KEY).value) + 0.1)) * weight));
    }

}
