package pfe.terrain.gen.algo.geometry;

import pfe.terrain.gen.algo.Mappable;

public class Edge extends Mappable {

    private Coord start;
    private Coord end;

    public Edge(Coord start, Coord end) {
        super();
        this.start = start;
        this.end = end;
    }

    public Coord getStart() {
        return start;
    }

    public Coord getEnd() {
        return end;
    }
}
