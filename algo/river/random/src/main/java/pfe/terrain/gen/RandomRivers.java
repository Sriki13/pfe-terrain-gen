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

import java.util.*;

public class RandomRivers extends Contract {

    public static final Param<Integer> nbRiversParam = new Param<>("nbRivers", Integer.class,
            "1-100", "Number of rivers in the island", 10);

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(nbRiversParam);
    }

    public static final Key<BooleanType> vertexWaterKey =
            new Key<>(verticesPrefix + "IS_WATER", BooleanType.class);
    public static final Key<DoubleType> heightKey =
            new Key<>(verticesPrefix + "HEIGHT", DoubleType.class);

    public static final Key<DoubleType> riverFlowKey =
            new SerializableKey<>(edgesPrefix + "RIVER_FLOW", "riverFlow", DoubleType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(vertices, seed, edges, vertexWaterKey, heightKey),
                asKeySet(riverFlowKey)
        );
    }

    @Override
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, KeyTypeMismatch, NoSuchKeyException {
        List<Coord> land = new ArrayList<>();
        List<Coord> allVertices = new ArrayList<>(map.getVertices());
        allVertices.sort((o1, o2) -> (int) (o1.x + o1.y - o2.x - o2.y));
        for (Coord vertex : allVertices) {
            if (vertex.getProperty(vertexWaterKey).value) {
                land.add(vertex);
            }
        }
        Random random = new Random(map.getSeed());
        int nbRivers = context.getParamOrDefault(nbRiversParam);
        for (int i = 0; i < nbRivers; i++) {
            generateRiverFrom(map.getEdges(), land.get(random.nextInt(land.size())));
        }
    }

    private void generateRiverFrom(Set<Edge> edges, Coord start)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        while (!start.getProperty(vertexWaterKey).value) {
            Coord flowTowards = getLowestNeighbour(edges, start);
            Edge edge = findEdge(edges, start, flowTowards);
            edge.putProperty(riverFlowKey, new DoubleType(1));
            start = flowTowards;
        }
    }

    private Coord getLowestNeighbour(Set<Edge> edges, Coord coord) throws NoSuchKeyException, KeyTypeMismatch {
        Set<Coord> neighbours = new HashSet<>();
        for (Edge edge : edges) {
            if (edge.getStart() == coord) {
                neighbours.add(edge.getEnd());
            } else if (edge.getEnd() == coord) {
                neighbours.add(edge.getStart());
            }
        }
        List<Coord> neighboursList = new ArrayList<>(neighbours);
        Coord min = neighboursList.get(0);
        for (int i = 1; i < neighboursList.size(); i++) {
            Coord current = neighboursList.get(i);
            if (current.getProperty(heightKey).value < min.getProperty(heightKey).value) {
                min = current;
            }
        }
        return min;
    }

    private Edge findEdge(Set<Edge> edges, Coord a, Coord b) {
        Edge searched = new Edge(a, b);
        for (Edge edge : edges) {
            if (edge.equals(searched)) {
                return edge;
            }
        }
        throw new RuntimeException("Could not find edge corresponding " +
                "to the Coordinates " + a + " and " + b);
    }

}
