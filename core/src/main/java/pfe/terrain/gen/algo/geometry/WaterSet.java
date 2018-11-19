package pfe.terrain.gen.algo.geometry;

import java.util.Collection;
import java.util.HashSet;

public class WaterSet extends HashSet<Face> {

    public WaterSet(Collection<? extends Face> c) {
        super(c);
    }

}
