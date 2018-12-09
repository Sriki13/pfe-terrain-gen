package pfe.terrain.gen.algo.island.geometry;

import java.util.Collection;
import java.util.HashSet;

public class FaceSet extends HashSet<Face> {

    public FaceSet() {
    }

    public FaceSet(Collection<Face> c) {
        super(c);
    }

}
