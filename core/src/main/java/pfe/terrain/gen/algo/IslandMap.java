package pfe.terrain.gen.algo;

import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.*;

public class IslandMap extends Mappable {

    private Integer size;
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
        this.size = null;
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
        if (key.equals(verticesKey)) {
            return key.getType().cast(vertices);
        } else if (key.equals(edgesKey)) {
            return key.getType().cast(edges);
        } else if (key.equals(facesKey)) {
            return key.getType().cast(faces);
        } else if (key.equals(sizeKey)) {
            return key.getType().cast(size);
        } else if (key.equals(seedKey)) {
            return key.getType().cast(seed);
        }
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

    public <T> boolean assertContaining(Key<T> key) {
        try {
            if (key.getId().startsWith(Contract.verticesPrefix)) {
                for (Coord coord : vertices) {
                    coord.getProperty(key);
                }
            } else if (key.getId().startsWith(Contract.edgesPrefix)) {
                for (Edge edge : edges) {
                    edge.getProperty(key);
                }
            } else if (key.getId().startsWith(Contract.facesPrefix)) {
                for (Face face : faces) {
                    face.getProperty(key);
                }
            } else {
                getProperty(key);
            }
        } catch (NoSuchKeyException | KeyTypeMismatch e) {
            return false;
        }
        return true;
    }
}
