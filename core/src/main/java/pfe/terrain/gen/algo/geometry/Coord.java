package pfe.terrain.gen.algo.geometry;

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

    public boolean matches(Coordinate coordinate) {
        return this.x == coordinate.x && this.y == coordinate.y;
    }

    @Override
    public String toString() {
        return "Coord{x=" + x + ", y=" + y + '}';
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
        return Objects.hash(x,y);
    }
}
