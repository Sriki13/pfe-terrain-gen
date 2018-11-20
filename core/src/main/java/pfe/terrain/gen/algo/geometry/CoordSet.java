package pfe.terrain.gen.algo.geometry;

import java.util.Collection;
import java.util.HashSet;

public class CoordSet extends HashSet<Coord> {

    public CoordSet() {
    }

    public CoordSet(Collection<Coord> c) {
        super(c);
    }
}
