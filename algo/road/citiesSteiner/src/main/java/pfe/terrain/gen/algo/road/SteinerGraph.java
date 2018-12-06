package pfe.terrain.gen.algo.road;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;

import java.util.*;

public class SteinerGraph {

    public Set<Edge> getSteinerGraph(DefaultUndirectedWeightedGraph<Coord, Edge> graph,
                                     List<Coord> cities, double roadConnections) {

        DefaultUndirectedWeightedGraph<Coord, Edge> subGraph = new DefaultUndirectedWeightedGraph<>(Edge.class);
        DefaultUndirectedWeightedGraph<Coord, Edge> finalGraph = new DefaultUndirectedWeightedGraph<>(Edge.class);

        for (Coord c : cities) {
            subGraph.addVertex(c);
            finalGraph.addVertex(c);
        }

        DijkstraShortestPath<Coord, Edge> shortestPath = new DijkstraShortestPath<>(graph);
        Map<Edge, GraphPath<Coord, Edge>> paths = new HashMap<>();

        // Step 1 : create a subgraph
        for (int i = 0; i < cities.size(); i++) {
            for (int j = i + 1; j < cities.size(); j++) {
                GraphPath<Coord, Edge> path = shortestPath.getPath(cities.get(i), cities.get(j));
                if (path != null) {
                    Edge e = new Edge(cities.get(i), cities.get(j));
                    paths.put(e, path);
                    subGraph.addEdge(cities.get(i), cities.get(j), e);
                    subGraph.setEdgeWeight(e, path.getWeight());
                }
            }
        }

        // Step 2 : get the minimum spanning tree of the subgraph
        PrimMinimumSpanningTree<Coord, Edge> prim = new PrimMinimumSpanningTree<>(subGraph);
        SpanningTreeAlgorithm.SpanningTree<Edge> spanningTree = prim.getSpanningTree();
        List<Edge> edgesByWeight = new ArrayList<>(subGraph.edgeSet());
        edgesByWeight.sort(Comparator.comparingDouble(subGraph::getEdgeWeight));
        for (int i = (int) Math.floor(edgesByWeight.size() / (1 + roadConnections / 2)) - 1; i > 0; i--) {
            subGraph.removeEdge(edgesByWeight.get(i));
        }
        Set<Edge> subEdges = new HashSet<>(subGraph.edgeSet());
        subEdges.addAll(spanningTree.getEdges());

        // Step 3 : replace the edges of the spanning tree by the corresponding path
        for (Edge e : subEdges) {
            if (paths.get(e) != null) {
                for (Coord c : paths.get(e).getVertexList()) {
                    finalGraph.addVertex(c);
                }
            }

        }
        for (Edge e : subEdges) {
            if (paths.get(e) != null) {
                for (Edge edge : paths.get(e).getEdgeList()) {
                    finalGraph.addEdge(edge.getStart(), edge.getEnd(), edge);
                    finalGraph.setEdgeWeight(edge, paths.get(e).getWeight());
                }
            }
        }

        return finalGraph.edgeSet();
    }
}
