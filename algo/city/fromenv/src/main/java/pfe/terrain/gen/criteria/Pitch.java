package pfe.terrain.gen.criteria;

import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Map;

import static pfe.terrain.gen.algo.constraints.Contract.FACES_PREFIX;

public class Pitch implements Criterion {

    public static final Key<DoubleType> PITCH_KEY =
            new Key<>(FACES_PREFIX + "PITCH", DoubleType.class);

    public static final Param<Double> CITY_PITCH_WEIGHT = Param.generateDefaultDoubleParam(
            "cityPitchWeight", "The multiplier applied to the penalty a location gets from not being flat",
            0.2, "Pitch bonus"
    );

    @Override
    public void assignScores(Context context, Map<Face, Double> scores) {
        double weight = context.getParamOrDefault(CITY_PITCH_WEIGHT);
        scores.forEach((key, value) ->
                scores.put(key, value - key.getProperty(PITCH_KEY).value * weight));
    }
}
