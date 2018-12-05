package pfe.terrain.gen.criteria;

import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static pfe.terrain.gen.algo.constraints.Contract.VERTICES_PREFIX;

public class HeightLevel implements Criterion {

    public static final Key<DoubleType> HEIGHT_KEY =
            new Key<>(VERTICES_PREFIX + "HEIGHT", DoubleType.class);

    public static final Param<Double> CITY_MIN_HEIGHT = Param.generateDefaultDoubleParam(
            "cityMinHeight", "The minimum height the cities tend to spawn. Faces above this min value and below the maximum will" +
                    "have more probability of spawning a city.", 0.1, "City minimum height"
    );

    public static final Param<Double> CITY_MAX_HEIGHT = Param.generateDefaultDoubleParam(
            "cityMaxHeight", "The maximum height the cities tend to spawn. Faces below this ax value and above the minimum will" +
                    "have more probability of spawning a city.", 0.7, "City maximum height"
    );

    public static final Param<Double> CITY_HEIGHT_WEIGHT = Param.generateDefaultDoubleParam(
            "cityHeightWeight", "The multiplier for the bonus a location gets from being in the correct height range",
            0.15, "Location height bonus"
    );

    private Map<Face, Double> normalizedHeight;

    public HeightLevel(Set<Face> land) {
        this.normalizedHeight = new HashMap<>();
        double maxHeight = Collections.max(land,
                (a, b) -> (int) (a.getCenter().getProperty(HEIGHT_KEY).value - b.getCenter().getProperty(HEIGHT_KEY).value))
                .getCenter().getProperty(HEIGHT_KEY).value;
        land.forEach(point -> normalizedHeight.put(point, point.getCenter()
                .getProperty(HEIGHT_KEY).value / maxHeight));
    }

    @Override
    public void assignScores(Context context, Map<Face, Double> scores) {
        double min = context.getParamOrDefault(CITY_MIN_HEIGHT);
        double max = context.getParamOrDefault(CITY_MAX_HEIGHT);
        if (min > max) {
            throw new InvalidAlgorithmParameters("City height maximum value is under the minimum value");
        }
        double weight = context.getParamOrDefault(CITY_HEIGHT_WEIGHT) * 100;
        scores.forEach((key, value) -> {
            double height = normalizedHeight.get(key);
            if (height >= min && height <= max) {
                scores.put(key, value + (1 / (height + 0.1)) * weight);
            }
        });
    }

}
