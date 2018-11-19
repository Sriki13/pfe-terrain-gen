package pfe.terrain.gen.algo.geometry;

import com.vividsolutions.jts.geom.Coordinate;

public class Edge {

    private Coordinate start;
    private Coordinate end;

    public Edge(Coordinate start, Coordinate end) {
        this.start = start;
        this.end = end;
    }

    public Coordinate getStart() {
        return start;
    }

    public Coordinate getEnd() {
        return end;
    }
}
