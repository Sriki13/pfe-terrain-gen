package pfe.terrain.gen.algo;

import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.EdgeSet;
import pfe.terrain.gen.algo.geometry.FaceSet;

public class IslandMap extends Mappable {

    private int size;
    private CoordSet vertices;
    private EdgeSet edges;
    private FaceSet faces;

    private Key<CoordSet> verticesKey = new Key<>("VERTICES", CoordSet.class);
    private Key<EdgeSet> edgesKey = new Key<>("EDGES", EdgeSet.class);
    private Key<FaceSet> facesKey = new Key<>("FACES", FaceSet.class);

    public IslandMap() {
        super();
        this.size = 0;
        this.vertices = null;
        this.edges = null;
        this.faces = null;
    }

    @Override
    public <T> void putProperty(Key<T> key, T value) throws DuplicateKeyException {
        if (key.equals(verticesKey)) {
            this.vertices = verticesKey.getType().cast(value);
        } else if (key.equals(edgesKey)) {
            this.edges = edgesKey.getType().cast(value);
        } else if (key.equals(facesKey)) {
            this.faces = facesKey.getType().cast(value);
        }
        super.putProperty(key, value);
    }

    @Override
    public <T> T getProperty(Key<T> key) throws NoSuchKeyException, KeyTypeMismatch {
        if (key.equals(verticesKey)) {
            return key.getType().cast(vertices);
        } else if (key.equals(edgesKey)) {
            return key.getType().cast(edges);
        } else if (key.equals(facesKey)) {
            return key.getType().cast(faces);
        }
        return super.getProperty(key);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
