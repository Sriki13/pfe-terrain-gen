package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.OptionalKey;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.types.*;

import java.util.Set;
import java.util.function.Function;

import static pfe.terrain.gen.algo.constraints.Contract.*;

public class RiverGenerator {

    // Required

    public static final Key<BooleanType> VERTEX_WATER_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    // Produced

    public static final Key<IntegerType> RIVER_FLOW_KEY =
            new SerializableKey<>(new OptionalKey<>(EDGES_PREFIX + "RIVER_FLOW", IntegerType.class), "riverFlow");

    public static final Key<MarkerType> IS_SOURCE_KEY =
            new OptionalKey<>(VERTICES_PREFIX + "SOURCE", MarkerType.class);

    public static final Key<MarkerType> IS_RIVER_END_KEY =
            new OptionalKey<>(VERTICES_PREFIX + "RIVER_END", MarkerType.class);


    private Key<DoubleType> heightKey;

    public RiverGenerator(TerrainMap terrainMap, Key<DoubleType> heightKey) {
        this.terrainMap = terrainMap;
        this.heightKey = heightKey;
    }

    private TerrainMap terrainMap;

    public void generateRiverFrom(Coord start, Set<Coord> seen) {
        generateRiverFrom(start, seen, (coord -> coord.getProperty(VERTEX_WATER_KEY).value));
    }

    public Coord generateRiverFrom(Coord start, Set<Coord> seen, Function<Coord, Boolean> endCondition) {
        start.putProperty(IS_SOURCE_KEY, new MarkerType());
        while (!endCondition.apply(start)) {
            Coord flowTowards = getLowestNeighbour(start, seen, true);
            if (flowTowards == start) {
                break;
            }
            seen.add(flowTowards);
            Edge edge = findEdge(start, flowTowards);
            edge.putProperty(RIVER_FLOW_KEY, new OptionalIntegerType(1));
            start = flowTowards;
        }
        start.putProperty(IS_RIVER_END_KEY, new MarkerType());
        return start;
    }

    public Coord getLowestNeighbour(Coord coord, Set<Coord> seen, boolean includeStart) {
        Set<Coord> neighbours = terrainMap.getProperty(EDGES).getConnectedVertices(coord);
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

    public Edge findEdge(Coord a, Coord b) {
        Edge searched = new Edge(a, b);
        for (Edge edge : terrainMap.getProperty(EDGES)) {
            if (edge.equals(searched)) {
                return edge;
            }
        }
        throw new IllegalArgumentException("Could not find edge corresponding " +
                "to the Coordinates " + a + " and " + b);
    }

}
