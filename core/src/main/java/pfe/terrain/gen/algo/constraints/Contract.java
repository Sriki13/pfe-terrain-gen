package pfe.terrain.gen.algo.constraints;

import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.EdgeSet;
import pfe.terrain.gen.algo.geometry.FaceSet;

public abstract class Contract {

    public static final String verticesPrefix = "VERTICES_";
    public static final String edgesPrefix = "EDGES_";
    public static final String facesPrefix = "FACES_";

    public static final Key<CoordSet> vertices = new Key<>("VERTICES", CoordSet.class);
    public static final Key<EdgeSet> edges = new Key<>("EDGES", EdgeSet.class);
    public static final Key<FaceSet> faces = new Key<>("FACES", FaceSet.class);

    public abstract Constraints getContract();

    public abstract void execute(IslandMap map) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch;

    public String getName() {
        return getClass().getName();
    }

}
