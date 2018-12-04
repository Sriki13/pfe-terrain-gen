package pfe.terrain.gen.criteria;

import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Edge;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.key.Key;
import pfe.terrain.gen.algo.types.IntegerType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static pfe.terrain.gen.algo.constraints.Contract.edgesPrefix;

public class RiverProximity implements Criterion {

    public static final Key<IntegerType> RIVER_FLOW_KEY =
            new Key<>(edgesPrefix + "RIVER_FLOW", IntegerType.class);


    private static final double WEIGHT = 1;

    private Set<Coord> riverPoints;

    public RiverProximity(Set<Edge> edges) {
        this.riverPoints = new HashSet<>();
        edges.forEach(edge -> {
            if (edge.getProperty(RIVER_FLOW_KEY).value > 0) {
                riverPoints.add(edge.getStart());
                riverPoints.add(edge.getEnd());
            }
        });
    }

    @Override
    public void assignScores(Map<Face, Double> scores) {
        riverPoints.forEach(riverPoint ->
                scores.forEach((key, value) ->
                        scores.put(key, value + (1 / riverPoint.distance(key.getCenter())) * WEIGHT)));
    }

}
