package pfe.terrain.gen.algo.road;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoadsToCitiesSteiner extends Contract {

    public static final Param<Double> ROAD_CONNECTIONS =
            Param.generateDefaultDoubleParam("roadConnections",
                    "How many side roads there are", 0.4, "Amount of roads");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(ROAD_CONNECTIONS);
    }


    private static final SerializableKey<DoubleType> FACE_PITCH_KEY =
            new SerializableKey<>(FACES_PREFIX + "PITCH", "pitch", DoubleType.class);

    private static final Key<DoubleType> VERTEX_HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    private static final Key<MarkerType> EDGE_IS_ROAD =
            new SerializableKey<>(new OptionalKey<>(EDGES_PREFIX + "IS_ROAD", MarkerType.class), "isRoad");

    public static final Key<MarkerType> CITY_KEY =
            new SerializableKey<>(new OptionalKey<>(FACES_PREFIX + "HAS_CITY", MarkerType.class), "isCity");


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(SIZE, EDGES, VERTICES, FACES, FACE_PITCH_KEY, SEED, VERTEX_HEIGHT_KEY, CITY_KEY),
                asKeySet(EDGE_IS_ROAD)
        );
    }

    @Override
    public String getDescription() {
        return "Creates road between cities based on a fast steiner graph with added edges, Complexity : O(T²(E+VlogV))";
    }

    @Override
    public void execute(TerrainMap terrainMap, Context context) {
        EdgeSet edges = terrainMap.getProperty(EDGES);
        CoordSet coords = terrainMap.getProperty(VERTICES);
        FaceSet faces = terrainMap.getProperty(FACES);
        Set<Coord> edgeCoords = new HashSet<>(coords);
        List<Coord> cities = new ArrayList<>();
        DefaultUndirectedWeightedGraph<Coord, Edge> graph = new DefaultUndirectedWeightedGraph<>(Edge.class);

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

        double h1, h2, dist, hdif, weight;
        double distNormalization = terrainMap.getProperty(SIZE) / 1600.0;
        for (Edge edge : edges) {
            h1 = edge.getStart().getProperty(VERTEX_HEIGHT_KEY).value;
            h2 = edge.getEnd().getProperty(VERTEX_HEIGHT_KEY).value;
            if (h1 <= 0 || h2 <= 0) {
                continue;
            }
            dist = Math.sqrt(Math.pow(edge.getStart().x - edge.getEnd().x, 2) + Math.pow(edge.getStart().y - edge.getEnd().y, 2));
            hdif = Math.abs(h1 - h2);
            if (hdif < 1) {
                hdif = 0;
            } else if (hdif < 2) {
                hdif -= 0.5;
            }
            weight = dist * distNormalization + (hdif * 4);
            graph.addEdge(edge.getStart(), edge.getEnd(), edge);
            graph.setEdgeWeight(edge, weight);
        }

        Set<Edge> edgeSet = new SteinerGraph().getSteinerGraph(graph, cities, context.getParamOrDefault(ROAD_CONNECTIONS));
        for (Edge e : edgeSet) {
            e.putProperty(EDGE_IS_ROAD, new MarkerType());
        }
    }
}
