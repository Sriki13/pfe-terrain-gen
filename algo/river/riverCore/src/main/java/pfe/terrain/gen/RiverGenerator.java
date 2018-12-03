package pfe.terrain.gen;

import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Edge;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.IntegerType;
import pfe.terrain.gen.algo.types.OptionalIntegerType;

import java.util.Set;

import static pfe.terrain.gen.algo.constraints.Contract.edgesPrefix;
import static pfe.terrain.gen.algo.constraints.Contract.verticesPrefix;

public class RiverGenerator {

    // Required

    public static final Key<BooleanType> vertexWaterKey =
            new SerializableKey<>(verticesPrefix + "IS_WATER", "isWater", BooleanType.class);

    public static final Key<DoubleType> heightKey =
            new SerializableKey<>(verticesPrefix + "HEIGHT", "height", DoubleType.class);


    // Produced

    public static final Key<IntegerType> riverFlowKey =
            new SerializableKey<>(edgesPrefix + "RIVER_FLOW", "riverFlow", IntegerType.class);

    public static final Key<Boolean> isSourceKey =
            new Key<>(verticesPrefix + "SOURCE", Boolean.class);

    public static final Key<Boolean> isRiverEndKey =
            new Key<>(verticesPrefix + "RIVER_END", Boolean.class);


    public RiverGenerator(IslandMap islandMap) {
        this.islandMap = islandMap;
    }

    private IslandMap islandMap;

    public Coord generateRiverFrom(Coord start, Set<Coord> seen) {
        start.putProperty(isSourceKey, true);
        while (!start.getProperty(vertexWaterKey).value) {
            Coord flowTowards = getLowestNeighbour(start, seen, true);
            if (flowTowards == start) {
                break;
            }
            seen.add(flowTowards);
            Edge edge = findEdge(start, flowTowards);
            edge.putProperty(riverFlowKey, new OptionalIntegerType(1));
            start = flowTowards;
        }
        start.putProperty(isRiverEndKey, true);
        return start;
    }

    public Coord getLowestNeighbour(Coord coord, Set<Coord> seen, boolean includeStart) {
        Set<Coord> neighbours = islandMap.getConnectedVertices(coord);
        Coord min = includeStart ? coord : null;
        for (Coord current : neighbours) {
            if (min == null ||
                    (!seen.contains(current) &&
                            current.getProperty(heightKey).value <= min.getProperty(heightKey).value)) {
                min = current;
            }
        }
        return min;
    }

    private Edge findEdge(Coord a, Coord b) {
        Edge searched = new Edge(a, b);
        for (Edge edge : islandMap.getEdges()) {
            if (edge.equals(searched)) {
                return edge;
            }
        }
        throw new RuntimeException("Could not find edge corresponding " +
                "to the Coordinates " + a + " and " + b);
    }

}
