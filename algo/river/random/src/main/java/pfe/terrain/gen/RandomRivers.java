package pfe.terrain.gen;

import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Edge;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.IntegerType;
import pfe.terrain.gen.algo.types.OptionalIntegerType;

import java.util.*;

public class RandomRivers extends Contract {

    public static final Param<Integer> nbRiversParam = new Param<>("nbRivers", Integer.class,
            "1-100", "Number of rivers in the island.", 10);

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(nbRiversParam);
    }

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


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(vertices, seed, edges, faces, vertexWaterKey, heightKey),
                asKeySet(riverFlowKey, isSourceKey, isRiverEndKey)
        );
    }

    public RandomRivers() {
    }

    public RandomRivers(IslandMap islandMap) {
        this.islandMap = islandMap;
    }

    private IslandMap islandMap;

    @Override
    public void execute(IslandMap map, Context context) {
        this.islandMap = map;
        Random random = new Random(map.getSeed());
        List<Coord> land = new ArrayList<>();
        for (Coord vertex : map.getVertices()) {
            vertex.putProperty(isSourceKey, false);
            vertex.putProperty(isRiverEndKey, false);
        }
        for (Coord vertex : map.getEdgeVertices()) {
            if (!vertex.getProperty(vertexWaterKey).value) {
                land.add(vertex);
            }
        }

        // necessary for deterministic purposes
        land.sort((o1, o2) -> (int) (o1.x + o1.y - o2.x - o2.y));

        for (Edge edge : map.getEdges()) {
            edge.putProperty(riverFlowKey, new OptionalIntegerType(0));
        }
        int nbRivers = context.getParamOrDefault(nbRiversParam);
        for (int i = 0; i < nbRivers; i++) {
            Coord start = land.get(random.nextInt(land.size()));
            while (start.getProperty(isSourceKey)) {
                start = land.get(random.nextInt(land.size()));
            }
            generateRiverFrom(start);
        }
    }

    public Coord generateRiverFrom(Coord start)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        Set<Coord> seen = new HashSet<>();
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

    public Coord getLowestNeighbour(Coord coord, Set<Coord> seen, boolean includeStart)
            throws NoSuchKeyException, KeyTypeMismatch {
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
