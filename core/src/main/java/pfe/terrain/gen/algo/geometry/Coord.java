package pfe.terrain.gen.algo.geometry;

import com.vividsolutions.jts.geom.Coordinate;
import pfe.terrain.gen.algo.Mappable;

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

    @Override
    public String toString() {
        return "Coord{x=" + x + ", y=" + y + '}';
    }
}
