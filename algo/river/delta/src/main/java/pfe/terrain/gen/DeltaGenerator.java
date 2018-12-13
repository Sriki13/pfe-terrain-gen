package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.OptionalKey;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.IntegerType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.*;
import java.util.stream.Collectors;

import static pfe.terrain.gen.RiverGenerator.*;

public class DeltaGenerator extends Contract {

    public static final Param<Integer> NB_DELTAS_PARAM = Param.generatePositiveIntegerParam("nbDeltas",
            50, "Number of deltas in the island.", 5, "Amount of deltas");

    public static final Param<Double> DELTA_HEIGHT = Param.generateDefaultDoubleParam("deltaHeight",
            "The maximum height the deltas can spawn", 0.2, "Delta maximum height");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(NB_DELTAS_PARAM, DELTA_HEIGHT);
    }

    public static final Key<MarkerType> DELTA_SOURCE_KEY = new OptionalKey<>(
            VERTICES_PREFIX + "IS_DELTA_SOURCE", MarkerType.class
    );

    public static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(VERTICES, SEED, EDGES, FACES, VERTEX_WATER_KEY, HEIGHT_KEY),
                asKeySet(DELTA_SOURCE_KEY),
                asKeySet(RIVER_FLOW_KEY, IS_SOURCE_KEY, IS_RIVER_END_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Adds river deltas to the island";
    }

    private RiverGenerator riverGenerator;
    private Set<Edge> lowEdges;

    @Override
    public void execute(TerrainMap map, Context context) {
        riverGenerator = new RiverGenerator(map, HEIGHT_KEY);
        Map<Coord, Double> normalized = normalizeHeights(map);
        Map<Coord, Set<Coord>> candidates = findDeltaCandidates(map, normalized, context.getParamOrDefault(DELTA_HEIGHT));
        Random random = new Random(map.getProperty(SEED));
        int nbDeltas = context.getParamOrDefault(NB_DELTAS_PARAM);
        List<Coord> randomList = new ArrayList<>(candidates.keySet());
        for (int i = 0; i < nbDeltas; i++) {
            if (randomList.isEmpty()) {
                break;
            }
            Coord delta = randomList.get(random.nextInt(randomList.size()));
            randomList.remove(delta);
            spawnDelta(delta, candidates.get(delta));
        }
    }

    private Map<Coord, Double> normalizeHeights(TerrainMap map) {
        double minHeight = Collections.min(map.getProperty(VERTICES),
                (a, b) -> (int) (1000 * (a.getProperty(HEIGHT_KEY).value - b.getProperty(HEIGHT_KEY).value)))
                .getProperty(HEIGHT_KEY).value;
        double maxHeight = Collections.max(map.getProperty(VERTICES),
                (a, b) -> (int) (1000 * (a.getProperty(HEIGHT_KEY).value - b.getProperty(HEIGHT_KEY).value)))
                .getProperty(HEIGHT_KEY).value;
        Map<Coord, Double> normalized = new HashMap<>();
        for (Coord coord : map.getProperty(VERTICES)) {
            normalized.put(coord, ((coord.getProperty(HEIGHT_KEY).value - minHeight) / maxHeight - minHeight));
        }
        return normalized;
    }

    private Map<Coord, Set<Coord>> findDeltaCandidates(TerrainMap map, Map<Coord, Double> normalized, double max) {
        lowEdges = map.getProperty(EDGES).stream()
                .filter(e -> normalized.get(e.getStart()) < max && normalized.get(e.getEnd()) < max)
                .collect(Collectors.toSet());
        Set<Edge> rivers = lowEdges.stream()
                .filter(e -> e.hasProperty(RIVER_FLOW_KEY))
                .collect(Collectors.toSet());
        Set<Coord> riverPoints = new HashSet<>();
        rivers.forEach(edge -> {
            riverPoints.add(edge.getStart());
            riverPoints.add(edge.getEnd());
        });
        Map<Coord, Set<Coord>> results = new HashMap<>();
        riverPoints.forEach(point -> {
            Set<Coord> lowNeighbors = findLowerNeighbors(lowEdges, riverPoints, point, normalized);
            if (!lowNeighbors.isEmpty()) {
                results.put(point, lowNeighbors);
            }
        });
        return results;
    }

    private Set<Coord> findLowerNeighbors(Set<Edge> edges, Set<Coord> exclude, Coord coord,
                                          Map<Coord, Double> normalized) {
        Coord otherEnd;
        double height = normalized.get(coord);
        Set<Coord> result = new HashSet<>();
        for (Edge edge : edges) {
            otherEnd = edge.getOtherEnd(coord);
            if (otherEnd != null && !exclude.contains(otherEnd) && normalized.get(otherEnd) < height) {
                result.add(otherEnd);
            }
        }
        return result;
    }

    private void spawnDelta(Coord point, Set<Coord> lowerNeighbors) {
        lowerNeighbors.forEach(neighbor -> {
            riverGenerator.generateRiverFrom(neighbor, new HashSet<>());
            findLowEdge(point, neighbor).putProperty(RIVER_FLOW_KEY, new IntegerType(1));
        });
        point.putProperty(DELTA_SOURCE_KEY, new MarkerType());
    }

    private Edge findLowEdge(Coord start, Coord end) {
        Edge searched = new Edge(start, end);
        for (Edge edge : lowEdges) {
            if (edge.equals(searched)) {
                return edge;
            }
        }
        throw new IllegalArgumentException("No edge matches these 2 coords");
    }


}
