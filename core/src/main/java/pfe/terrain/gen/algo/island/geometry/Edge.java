package pfe.terrain.gen.algo.island.geometry;

import pfe.terrain.gen.algo.Mappable;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return (Objects.equals(start, edge.start) && Objects.equals(end, edge.end))
                || (Objects.equals(start, edge.end) && Objects.equals(end, edge.start));
    }

    @Override
    public int hashCode() {
        return Objects.hash((int) start.x + (int) end.x);
    }

}
