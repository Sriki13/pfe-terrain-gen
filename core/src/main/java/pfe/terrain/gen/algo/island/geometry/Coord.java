package pfe.terrain.gen.algo.island.geometry;

import com.vividsolutions.jts.geom.Coordinate;
import pfe.terrain.gen.algo.Mappable;

import java.util.Objects;

public class Coord extends Mappable {

    public double x;
    public double y;

    public Coord(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }

    public Coord(Coordinate wrapped) {
        super();
        this.x = wrapped.x;
        this.y = wrapped.y;
    }

    public double distance(Coord other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    @Override
    public String toString() {
        return "{" + x + "," + y + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return Double.compare(coord.x, x) == 0 &&
                Double.compare(coord.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
