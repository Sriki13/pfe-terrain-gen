package pfe.terrain.gen.algo.geometry;

import com.vividsolutions.jts.geom.Coordinate;

public class Edge {
    private Coordinate a;
    private Coordinate b;

    public Edge(Coordinate a, Coordinate b) {
        this.a = a;
        this.b = b;
    }
}
