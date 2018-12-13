package pfe.terrain.gen.cave;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultUndirectedGraph;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SmallIslandSuppression extends Contract {

    private static final Param<Integer> SUPPRESSION_THRESHOLD = Param.generatePositiveIntegerParam("suppressionThreshold", 20,
            "How big an island must be to be kept in the map", 3, "Suppresion Threshold");


    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(SUPPRESSION_THRESHOLD);
    }

    private static final Key<BooleanType> FACE_WATER_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    private static final Key<BooleanType> VERTEX_WATER_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    private static final Key<WaterKind> WATER_KIND_KEY =
            new SerializableKey<>(FACES_PREFIX + "WATER_KIND", "waterKind", WaterKind.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, VERTICES, SEED),
                asKeySet(),
                asKeySet(FACE_WATER_KEY, VERTEX_WATER_KEY, WATER_KIND_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Removes islands smaller than a threshold";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        int supprThreshold = context.getParamOrDefault(SUPPRESSION_THRESHOLD);
        DefaultUndirectedGraph<Coord, Edge> graph = buildCaveGraph(map);
        ConnectivityInspector<Coord, Edge> connectivityInspector = new ConnectivityInspector<>(graph);

        List<Set<Coord>> islands = connectivityInspector.connectedSets();

        for (Set<Coord> island : islands) {
            if (island.size() < supprThreshold) {
                island.forEach(i -> i.putProperty(VERTEX_WATER_KEY, new BooleanType(true)));
            }
        }

        for (Face face : map.getProperty(FACES)) {
            boolean anyLand = face.getBorderVertices().stream()
                    .anyMatch(c -> !c.getProperty(VERTEX_WATER_KEY).value);
            if (!anyLand) {
                face.getCenter().putProperty(VERTEX_WATER_KEY, new BooleanType(true));
                face.putProperty(WATER_KIND_KEY, WaterKind.OCEAN);
                face.putProperty(FACE_WATER_KEY, new BooleanType(true));
            }
        }
    }

    private DefaultUndirectedGraph<Coord, Edge> buildCaveGraph(TerrainMap map) {
        DefaultUndirectedGraph<Coord, Edge> graph = new DefaultUndirectedGraph<>(Edge.class);
        Set<Coord> edgeCoords = new HashSet<>();

        for (Face face : map.getProperty(FACES)) {
            edgeCoords.addAll(face.getBorderVertices());
        }

        for (Coord coord : edgeCoords) {
            if (!coord.getProperty(VERTEX_WATER_KEY).value) {
                graph.addVertex(coord);
            }
        }

        for (Edge edge : map.getProperty(EDGES)) {
            if (!edge.getStart().getProperty(VERTEX_WATER_KEY).value && !edge.getEnd().getProperty(VERTEX_WATER_KEY).value) {
                graph.addEdge(edge.getStart(), edge.getEnd(), edge);
            }
        }

        return graph;
    }
}
