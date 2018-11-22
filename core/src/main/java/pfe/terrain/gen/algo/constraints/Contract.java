package pfe.terrain.gen.algo.constraints;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.EdgeSet;
import pfe.terrain.gen.algo.geometry.FaceSet;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Contract implements Parameters {

    public static Set<Key> asSet(Key... params) {
        return Stream.of(params).collect(Collectors.toSet());
    }

    protected static final String verticesPrefix = "VERTICES_";
    protected static final String edgesPrefix = "EDGES_";
    protected static final String facesPrefix = "FACES_";

    public static final Key<CoordSet> vertices = new Key<>("VERTICES", CoordSet.class);
    public static final Key<EdgeSet> edges = new Key<>("EDGES", EdgeSet.class);
    public static final Key<FaceSet> faces = new Key<>("FACES", FaceSet.class);
    public static final Key<Integer> size = new Key<>("SIZE", Integer.class);
    public static final Key<Integer> seed = new Key<>("SEED", Integer.class);

    public abstract Constraints getContract();

    public abstract void execute(IslandMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch;

    public String getName() {
        return getClass().getName();
    }

}
