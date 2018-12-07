package pfe.terrain.gen.algo.road;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.OptionalKey;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.*;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.*;

public class BridgesBuilder extends Contract {

    public static final Param<Double> MAX_BRIDGE_LENGTH =
            Param.generateDefaultDoubleParam("maxBridgeLength",
                    "How long can the bridge be", 0.4, "Max length of the bridges");

    public static final Param<Integer> MIN_CITIES_FOR_BRIDGE =
            Param.generatePositiveIntegerParam("minCitiesForBridge", 8,
                    "How many cities are needed by island to construct a bridge between them",
                    1, "Minimum number of cities to build bridge");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(MAX_BRIDGE_LENGTH, MIN_CITIES_FOR_BRIDGE);
    }

    private static final Key<DoubleType> VERTEX_HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    private static final Key<MarkerType> CITY_KEY =
            new SerializableKey<>(new OptionalKey<>(FACES_PREFIX + "HAS_CITY", MarkerType.class), "isCity");

    private static final Key<MarkerType> BRIDGES_KEY =
            new OptionalKey<>("HAS_BRIDGE", MarkerType.class);


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(SIZE, EDGES, VERTICES, FACES, SEED, VERTEX_HEIGHT_KEY, CITY_KEY),
                asKeySet(BRIDGES_KEY),
                asKeySet(VERTEX_HEIGHT_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Creates road between cities based on a fast steiner graph with added edges, Complexity : O(I*V²) optimized";
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void execute(TerrainMap terrainMap, Context context) {

        EdgeSet edges = terrainMap.getProperty(EDGES);
        CoordSet coords = terrainMap.getProperty(VERTICES);
        FaceSet faces = terrainMap.getProperty(FACES);
        Set<Coord> edgeCoords = new HashSet<>(coords);
        List<Coord> cities = new ArrayList<>();

        DefaultUndirectedGraph<Coord, Edge> graph = new DefaultUndirectedGraph<Coord, Edge>(Edge.class);

        ConnectivityInspector<Coord, Edge> connectivityInspector = new ConnectivityInspector<>(graph);

        // Step 1 : Create a graph with emerged land to find islands and cities
        for (Face face : faces) {
            edgeCoords.remove(face.getCenter());
            if (face.hasProperty(CITY_KEY)) {
                for (Coord city : face.getBorderVertices()) {
                    if (city.getProperty(VERTEX_HEIGHT_KEY).value > 0) {
                        cities.add(city);
                        break;
                    }
                }
            }
        }

        for (Coord coord : edgeCoords) {
            if (coord.getProperty(VERTEX_HEIGHT_KEY).value > 0) {
                graph.addVertex(coord);
            }
        }

        for (Edge edge : edges) {
            if (edge.getStart().getProperty(VERTEX_HEIGHT_KEY).value > 0 && edge.getEnd().getProperty(VERTEX_HEIGHT_KEY).value > 0) {
                graph.addEdge(edge.getStart(), edge.getEnd(), edge);
            }
        }


        // Step 2 : Divide graph into disjoint sets, remove islands with no city using set intersection
        List<Set<Coord>> islands = connectivityInspector.connectedSets();
        Map<Set<Coord>, Boolean> mapToIsland = new HashMap<>();
        int minNumberOfCity = context.getParamOrDefault(MIN_CITIES_FOR_BRIDGE);

        for (Set<Coord> islandPoints : islands) {
            Set<Coord> intersection = new HashSet<>(cities);
            intersection.retainAll(islandPoints);
            if (intersection.size() >= minNumberOfCity) {
                cities.removeAll(intersection);
                mapToIsland.put(islandPoints, false);
            } else {
                mapToIsland.put(islandPoints, true);
            }
        }
        for (Map.Entry<Set<Coord>, Boolean> entry : mapToIsland.entrySet()) {
            if (entry.getValue()) {
                islands.remove(entry.getKey());
            }
        }

        // Step 3 : Reduce set number of vertices by deleting those who are too high (keep only coasts) and order set
        //          by size of the coast = big island first
        int minHeightOptimization = 10;
        islands.sort(Comparator.comparingInt(Set::size));
        islands.forEach(coordSet -> coordSet.removeIf(c -> c.getProperty(VERTEX_HEIGHT_KEY).value >= minHeightOptimization));


        // Step 4 : Calculate the closest pair of point (Edge) between the source island (the biggest one) and all other
        //          islands with cities on it, if the euclidian distance is too long, do not add it to the bridges
        double dist;
        double maxBridgeLength = context.getParamOrDefault(MAX_BRIDGE_LENGTH);
        Set<Edge> bridges = new HashSet<>();
        EdgeDist minEdge;
        if (islands.size() > 1) {
            for (int i = 0; i < islands.size() - 1; i++) {
                minEdge = new EdgeDist(new Edge(null, null), Double.MAX_VALUE);
                for (Coord c1 : islands.get(islands.size() - 1)) {
                    for (Coord c2 : islands.get(i)) {
                        dist = c1.distance(c2);
                        if (dist < minEdge.getLength()) {
                            minEdge = new EdgeDist(new Edge(c1, c2), dist);
                        }
                    }
                }
                if (minEdge.getLength() < 80 * (maxBridgeLength + 0.1)) {
                    bridges.add(minEdge.getEdge());
                }
            }
        }

        // Step 5 : Create a graph with water and low altitude land (to contain also the coast computed before
        DefaultUndirectedWeightedGraph<Coord, Edge> bridgeGraph = new DefaultUndirectedWeightedGraph<>(Edge.class);
        for (Coord coord : edgeCoords) {
            if (coord.getProperty(VERTEX_HEIGHT_KEY).value < minHeightOptimization) {
                bridgeGraph.addVertex(coord);
            }
        }

        for (Edge edge : edges) {
            if (edge.getStart().getProperty(VERTEX_HEIGHT_KEY).value < minHeightOptimization && edge.getEnd().getProperty(VERTEX_HEIGHT_KEY).value < minHeightOptimization) {
                bridgeGraph.addEdge(edge.getStart(), edge.getEnd(), edge);
                bridgeGraph.setEdgeWeight(edge, edge.getStart().distance(edge.getEnd()));
            }
        }

        // Step 6 : Find the Dijkstra shortest path in this graph between the points computed earlier and mark all edges
        //          used in the path as bridge (make it higher)
        DijkstraShortestPath<Coord, Edge> shortestPath = new DijkstraShortestPath<>(bridgeGraph);

        List<Edge> bridgeEdges = new ArrayList<>();
        for (Edge e : bridges) {
            bridgeEdges.addAll(shortestPath.getPath(e.getStart(), e.getEnd()).getEdgeList());
        }
        for (Edge e : bridgeEdges) {
            e.getStart().putProperty(VERTEX_HEIGHT_KEY, new DoubleType(9.0));
            e.getEnd().putProperty(VERTEX_HEIGHT_KEY, new DoubleType(9.0));
        }
    }
}
