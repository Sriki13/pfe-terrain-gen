package pfe.terrain.gen.criteria;

import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Map;

import static pfe.terrain.gen.algo.constraints.Contract.FACES_PREFIX;

public class MoistureLevel implements Criterion {

    public static final Key<DoubleType> MOISTURE_KEY =
            new Key<>(FACES_PREFIX + "MOISTURE", DoubleType.class);

    public static final double IDEAL = 0.5;
    private static final double WEIGHT = 0.1;

    @Override
    public void assignScores(Map<Face, Double> scores) {
        scores.forEach((key, value) ->
                scores.put(key, value + (1 / (Math.abs(IDEAL - key.getProperty(MOISTURE_KEY).value) + 0.1)) * WEIGHT));
    }

}
