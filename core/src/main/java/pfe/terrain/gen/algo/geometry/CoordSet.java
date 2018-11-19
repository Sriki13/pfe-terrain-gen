package pfe.terrain.gen.algo.geometry;

import com.vividsolutions.jts.geom.Coordinate;

import java.util.Collection;
import java.util.HashSet;

public class CoordSet extends HashSet<Coordinate> {
    public CoordSet() {
    }

    public CoordSet(Collection<? extends Coordinate> c) {
        super(c);
    }
}
