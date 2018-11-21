package pfe.terrain.gen.algo.geometry;

import com.vividsolutions.jts.geom.Coordinate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CoordSet extends HashSet<Coord> {

    public CoordSet() {
    }

    public CoordSet(Collection<Coord> c) {
        super(c);
    }

    public static CoordSet buildFromCoordinates(Set<Coordinate> c) {
        return c.stream().map(coordinate -> new Coord(coordinate.x, coordinate.y)).collect(Collectors.toCollection(CoordSet::new));
    }

    public Set<Coordinate> convertToCoordinateSet() {
        return this.stream().map(coord -> new Coordinate(coord.x, coord.y)).collect(Collectors.toSet());
    }
}
