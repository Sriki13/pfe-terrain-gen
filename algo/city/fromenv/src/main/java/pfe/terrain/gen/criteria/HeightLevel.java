package pfe.terrain.gen.criteria;

import pfe.terrain.gen.algo.constraints.key.Key;
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

    public static final double RANGE_START = 0.1;
    public static final double RANGE_END = 0.5;

    private static final int WEIGHT = 1;

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
    public void assignScores(Map<Face, Double> scores) {
        scores.forEach((key, value) -> {
            double height = normalizedHeight.get(key);
            if (height >= RANGE_START && height <= RANGE_END) {
                scores.put(key, value + (1 / (height + 0.1)) * WEIGHT);
            }
        });
    }

}
