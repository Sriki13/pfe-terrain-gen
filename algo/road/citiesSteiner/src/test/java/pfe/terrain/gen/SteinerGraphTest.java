package pfe.terrain.gen;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.road.SteinerGraph;

import java.util.Arrays;
import java.util.List;

public class SteinerGraphTest {

    private DefaultUndirectedWeightedGraph<Coord, Edge> g;
    private SteinerGraph steinerGraph;

    @Before
    public void setUp() {
        g = new DefaultUndirectedWeightedGraph<>(Edge.class);
        steinerGraph = new SteinerGraph();
    }

    @Test
    public void testo() {
        Coord c1 = new Coord(0.0, 1.0);
        Coord c2 = new Coord(0.0, 2.0);
        Coord c3 = new Coord(0.0, 3.0);
        Coord c4 = new Coord(0.0, 4.0);
        Coord c5 = new Coord(0.0, 5.0);
        Coord c6 = new Coord(0.0, 6.0);
        g.addVertex(c1);
        g.addVertex(c2);
        g.addVertex(c3);
        g.addVertex(c4);
        g.addVertex(c5);
        g.addVertex(c6);
        Edge e1 = new Edge(c1, c2);
        Edge e2 = new Edge(c2, c3);
        Edge e3 = new Edge(c3, c4);
        Edge e4 = new Edge(c4, c5);
        Edge e5 = new Edge(c5, c6);
        Edge e6 = new Edge(c6, c1);
        Edge e7 = new Edge(c3, c6);
        Edge e8 = new Edge(c1, c3);
        g.addEdge(c1, c2, e1);
        g.addEdge(c2, c3, e2);
        g.addEdge(c3, c4, e3);
        g.addEdge(c4, c5, e4);
        g.addEdge(c5, c6, e5);
        g.addEdge(c6, c1, e6);
        g.addEdge(c3, c6, e7);
        g.addEdge(c1, c3, e8);
        g.setEdgeWeight(e1, 10.0);
        g.setEdgeWeight(e2, 2.0);
        g.setEdgeWeight(e3, 4.0);
        g.setEdgeWeight(e4, 10.0);
        g.setEdgeWeight(e5, 5.0);
        g.setEdgeWeight(e6, 20.0);
        g.setEdgeWeight(e7, 6.0);
        g.setEdgeWeight(e8, 3.0);
        List<Coord> cities = Arrays.asList(c1, c6,c2);
        System.out.println(steinerGraph.getSteinerGraph(g, cities));
    }
}
