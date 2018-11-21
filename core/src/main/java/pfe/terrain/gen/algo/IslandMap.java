package pfe.terrain.gen.algo;

import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.EdgeSet;
import pfe.terrain.gen.algo.geometry.FaceSet;

public class IslandMap extends Mappable {

    private int size;
    private int seed;
    private CoordSet vertices;
    private EdgeSet edges;
    private FaceSet faces;

    private Key<CoordSet> verticesKey = new Key<>("VERTICES", CoordSet.class);
    private Key<EdgeSet> edgesKey = new Key<>("EDGES", EdgeSet.class);
    private Key<FaceSet> facesKey = new Key<>("FACES", FaceSet.class);
    private Key<Integer> sizeKey = new Key<>("SIZE", Integer.class);
    private Key<Integer> seedKey = new Key<>("SEED", Integer.class);

    public IslandMap() {
        super();
        this.size = 0;
        this.vertices = null;
        this.edges = null;
        this.faces = null;
        this.seed = 0;
    }

    @Override
    public <T> void putProperty(Key<T> key, T value) throws DuplicateKeyException {
        if (key.equals(verticesKey)) {
            this.vertices = verticesKey.getType().cast(value);
        } else if (key.equals(edgesKey)) {
            this.edges = edgesKey.getType().cast(value);
        } else if (key.equals(facesKey)) {
            this.faces = facesKey.getType().cast(value);
        } else if (key.equals(sizeKey)) {
            this.size = sizeKey.getType().cast(value);
        } else if (key.equals(seedKey)) {
            this.seed = seedKey.getType().cast(value);
        }
        super.putProperty(key, value);
    }

    @Override
    public <T> T getProperty(Key<T> key) throws NoSuchKeyException, KeyTypeMismatch {
        return super.getProperty(key);
    }

    public CoordSet getVertices() {
        return vertices;
    }

    public EdgeSet getEdges() {
        return edges;
    }

    public FaceSet getFaces() {
        return faces;
    }

    public int getSeed() {
        return seed;
    }

    public int getSize() {
        return size;
    }
}
