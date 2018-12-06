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
                                     List<Coord> cities) {

        DefaultUndirectedWeightedGraph<Coord, Edge> subGraph = new DefaultUndirectedWeightedGraph<>(Edge.class);
        DefaultUndirectedWeightedGraph<Coord, Edge> finalGraph = new DefaultUndirectedWeightedGraph<>(Edge.class);

        for (Coord c : cities) {
            subGraph.addVertex(c);
            finalGraph.addVertex(c);
        }

        DijkstraShortestPath<Coord, Edge> shortestPath = new DijkstraShortestPath<>(graph);
        Map<Coord, GraphPath<Coord, Edge>> paths = new HashMap<>();

        // Step 1 : create a subgraph
        for (int i = 0; i < cities.size(); i++) {
            for (int j = i + 1; j < cities.size(); j++) {
                Edge e = new Edge(cities.get(i), cities.get(j));
                subGraph.addEdge(cities.get(i), cities.get(j), e);
                GraphPath<Coord, Edge> path = shortestPath.getPath(cities.get(i), cities.get(j));
                paths.put(cities.get(i), path);
                subGraph.setEdgeWeight(e, path.getWeight());
            }
        }

        // Step 2 : get the minimum spanning tree of the subgraph

//        List<Edge> edgesByWeight = new ArrayList<>(subGraph.edgeSet());
//        edgesByWeight.sort((o1, o2) -> (int) (1000 * (subGraph.getEdgeWeight(o1) - subGraph.getEdgeWeight(o2))));
//        for (int i = 0; i < edgesByWeight.size()/2; i++) {
//            subGraph.removeEdge(edgesByWeight.get(i));
//        }
        PrimMinimumSpanningTree<Coord, Edge> prim = new PrimMinimumSpanningTree<>(subGraph);
        SpanningTreeAlgorithm.SpanningTree<Edge> spanningTree = prim.getSpanningTree();

        // Step 3 : replace the edges of the spanning tree by the corresponding path
        Set<Coord> found = new HashSet<>(paths.keySet());
        for (Edge e : spanningTree) {
            if (found.contains(e.getStart())) {
                for (Coord c : paths.get(e.getStart()).getVertexList()) {
                    finalGraph.addVertex(c);
                }
                found.remove(e.getStart());
            }
            if (found.contains(e.getEnd())) {
                for (Coord c : paths.get(e.getEnd()).getVertexList()) {
                    finalGraph.addVertex(c);
                }
                found.remove(e.getEnd());
            }
        }
        found = new HashSet<>(paths.keySet());
        for (Edge e : spanningTree) {
            if (found.contains(e.getStart())) {
                for (Edge edge : paths.get(e.getStart()).getEdgeList()) {
                    finalGraph.addEdge(edge.getStart(), edge.getEnd(), edge);
                    finalGraph.setEdgeWeight(edge, paths.get(e.getStart()).getWeight());
                }
            }
            if (found.contains(e.getEnd())) {
                for (Edge edge : paths.get(e.getEnd()).getEdgeList()) {
                    finalGraph.addEdge(edge.getStart(), edge.getEnd(), edge);
                    finalGraph.setEdgeWeight(edge, paths.get(e.getEnd()).getWeight());
                }
            }
        }

        // Step 4 : get the minmum spanning tree of the finalGraph
        PrimMinimumSpanningTree<Coord, Edge> finalPrim = new PrimMinimumSpanningTree<>(finalGraph);
        SpanningTreeAlgorithm.SpanningTree<Edge> finalSpanningTree = finalPrim.getSpanningTree();

        for (Edge e : finalGraph.edgeSet()) {
            if (!finalSpanningTree.getEdges().contains(e)) {
                finalGraph.removeEdge(e);
            }
        }

        // Step 5 : remove leaves who are not cities
        while (true) {
            int i = 0;
            for (Coord c : finalGraph.vertexSet()) {
                if (finalGraph.degreeOf(c) == 1 && !cities.contains(c)) {
                    finalGraph.removeVertex(c);
                    i++;
                }
            }
            if (i == 0) {
                break;
            }
        }

        // add some of the missing paths

        return finalGraph.edgeSet();
    }
}
