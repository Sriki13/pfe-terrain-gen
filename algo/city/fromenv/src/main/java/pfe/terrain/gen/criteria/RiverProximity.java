package pfe.terrain.gen.criteria;

import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.IntegerType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static pfe.terrain.gen.algo.constraints.Contract.EDGES_PREFIX;

public class RiverProximity implements Criterion {

    public static final Key<IntegerType> RIVER_FLOW_KEY =
            new Key<>(EDGES_PREFIX + "RIVER_FLOW", IntegerType.class);

    public static final Param<Double> CITY_RIVER_WEIGHT = Param.generateDefaultDoubleParam(
            "cityRiverWeight", "The multiplier applied to the bonus a location gets from being close to rivers",
            0.1, "River bonus"
    );

    private Set<Coord> riverPoints;

    public RiverProximity(Set<Edge> edges) {
        this.riverPoints = new HashSet<>();
        edges.forEach(edge -> {
            if (edge.hasProperty(RIVER_FLOW_KEY)) {
                riverPoints.add(edge.getStart());
                riverPoints.add(edge.getEnd());
            }
        });
    }

    @Override
    public void assignScores(Context context, Map<Face, Double> scores) {
        double weight = context.getParamOrDefault(CITY_RIVER_WEIGHT) * 100;
        riverPoints.forEach(riverPoint ->
                scores.forEach((key, value) ->
                        scores.put(key, value + (1 / riverPoint.distance(key.getCenter())) * weight)));
    }

}
