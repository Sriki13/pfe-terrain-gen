package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.OptionalKey;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.types.*;

import java.util.Set;

import static pfe.terrain.gen.algo.constraints.Contract.EDGES_PREFIX;
import static pfe.terrain.gen.algo.constraints.Contract.VERTICES_PREFIX;

public class RiverGenerator {

    // Required

    public static final Key<BooleanType> VERTEX_WATER_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    public static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    // Produced

    public static final Key<IntegerType> RIVER_FLOW_KEY =
            new SerializableKey<>(new OptionalKey<>(EDGES_PREFIX + "RIVER_FLOW", IntegerType.class), "riverFlow");

    public static final Key<MarkerType> IS_SOURCE_KEY =
            new OptionalKey<>(VERTICES_PREFIX + "SOURCE", MarkerType.class);

    public static final Key<MarkerType> IS_RIVER_END_KEY =
            new OptionalKey<>(VERTICES_PREFIX + "RIVER_END", MarkerType.class);


    public RiverGenerator(IslandMap islandMap) {
        this.islandMap = islandMap;
    }

    private IslandMap islandMap;

    public void generateRiverFrom(Coord start, Set<Coord> seen) {
        start.putProperty(IS_SOURCE_KEY, new MarkerType());
        while (!start.getProperty(VERTEX_WATER_KEY).value) {
            Coord flowTowards = getLowestNeighbour(start, seen, true);
            if (flowTowards == start) {
                break;
            }
            seen.add(flowTowards);
            Edge edge = islandMap.findEdge(start, flowTowards);
            edge.putProperty(RIVER_FLOW_KEY, new OptionalIntegerType(1));
            start = flowTowards;
        }
        start.putProperty(IS_RIVER_END_KEY, new MarkerType());
    }

    public Coord getLowestNeighbour(Coord coord, Set<Coord> seen, boolean includeStart) {
        Set<Coord> neighbours = islandMap.getConnectedVertices(coord);
        Coord min = includeStart ? coord : null;
        for (Coord current : neighbours) {
            if (min == null ||
                    (!seen.contains(current) &&
                            current.getProperty(HEIGHT_KEY).value <= min.getProperty(HEIGHT_KEY).value)) {
                min = current;
            }
        }
        return min;
    }

}
