package pfe.terrain.gen.cave;

import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static pfe.terrain.gen.algo.constraints.Contract.*;

public class GraphLib {

    private static final Key<BooleanType> VERTEX_WALL_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WALL", "isWall", BooleanType.class);


    public static DefaultUndirectedGraph<Coord, Edge> buildCaveGraph(TerrainMap map) {
        DefaultUndirectedGraph<Coord, Edge> graph = new DefaultUndirectedGraph<>(Edge.class);
        Set<Coord> edgeCoords = new HashSet<>(map.getProperty(VERTICES));

        for (Face face : map.getProperty(FACES)) {
            edgeCoords.remove(face.getCenter());
        }

        for (Coord coord : edgeCoords) {
            if (!coord.getProperty(VERTEX_WALL_KEY).value) {
                graph.addVertex(coord);
            }
        }

        for (Edge edge : map.getProperty(EDGES)) {
            if (!edge.getStart().getProperty(VERTEX_WALL_KEY).value && !edge.getEnd().getProperty(VERTEX_WALL_KEY).value) {
                graph.addEdge(edge.getStart(), edge.getEnd(), edge);
            }
        }

        return graph;
    }


    public static DefaultUndirectedWeightedGraph<Coord, Edge> buildFullGraphRandomWeight(TerrainMap map, Random random) {
        DefaultUndirectedWeightedGraph<Coord, Edge> graph = new DefaultUndirectedWeightedGraph<>(Edge.class);
        Set<Coord> edgeCoords = new HashSet<>(map.getProperty(VERTICES));

        for (Face face : map.getProperty(FACES)) {
            edgeCoords.remove(face.getCenter());
        }

        for (Coord coord : edgeCoords) {
            graph.addVertex(coord);
        }

        for (Edge edge : map.getProperty(EDGES)) {
            graph.addEdge(edge.getStart(), edge.getEnd(), edge);
            graph.setEdgeWeight(edge, random.nextDouble() + 0.01);
        }
        return graph;
    }
}
