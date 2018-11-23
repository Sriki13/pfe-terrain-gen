package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.SerializableKey;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.*;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.HashSet;
import java.util.Set;

public class HeightFromWater extends Contract {

    public static final Key<BooleanType> verticeBorderKey =
            new Key<>(verticesPrefix + "IS_BORDER", BooleanType.class);
    public static final Key<BooleanType> faceBorderKey =
            new Key<>(facesPrefix + "IS_BORDER", BooleanType.class);

    public static final Key<DoubleType> vertexHeightKey =
            new SerializableKey<>(verticesPrefix + "HEIGHT", "height", DoubleType.class);

    public static final Key<BooleanType> faceWaterKey = new Key<>(facesPrefix + "IS_WATER", BooleanType.class);
    public static final Key<BooleanType> vertexWaterKey = new Key<>(verticesPrefix + "IS_WATER", BooleanType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asSet(faces, edges, vertices, faceBorderKey, vertexWaterKey),
                asSet(vertexHeightKey)
        );
    }

    @Override
    public void execute(IslandMap map, Context context)
            throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        Set<Coord> coordsToProcess = new HashSet<>();
        double height = 0.0;
        CoordSet vertices = map.getVertices();
        for (Coord vertex : vertices) {
            if (vertex.getProperty(vertexWaterKey).value) {
                vertex.putProperty(vertexHeightKey, new DoubleType(height));
            } else {
                coordsToProcess.add(vertex);
            }
        }
        EdgeSet allEdges = new EdgeSet(map.getEdges());
        EdgeSet edgesToProcess;
        int coordsSize = -1;

        // If coordsToProcess size wasn't reduced then only centers are remaining
        while (!(coordsSize == coordsToProcess.size())) {
            edgesToProcess = getEdgesToProcess(coordsToProcess, allEdges);
            coordsSize = coordsToProcess.size();
            for (Edge e : edgesToProcess) {
                if (coordsToProcess.contains(e.getStart())) {
                    e.getStart().putProperty(vertexHeightKey, new DoubleType(height));
                    coordsToProcess.remove(e.getStart());
                } else {
                    e.getEnd().putProperty(vertexHeightKey, new DoubleType(height));
                    coordsToProcess.remove(e.getEnd());
                }
                allEdges.remove(e);
            }
            height+=5.1;
        }

        // Set center as mean height to conform
        for (Face face : map.getFaces()) {
            double sum = 0;
            int total = 0;
            for (Coord border : face.getVertices()) {
                sum += border.getProperty(vertexHeightKey).value;
                total++;
            }
            face.getCenter().putProperty(vertexHeightKey, new DoubleType(sum / (double) total));
        }
    }

    private EdgeSet getEdgesToProcess(Set<Coord> coordsToProcess, EdgeSet edges) {
        EdgeSet edgesToProcess = new EdgeSet();
        for (Edge e : edges) {
            if (coordsToProcess.contains(e.getStart())) {
                if (!coordsToProcess.contains(e.getEnd())) {
                    edgesToProcess.add(e);
                }
            } else if (coordsToProcess.contains(e.getEnd())) {
                edgesToProcess.add(e);
            }
        }
        return edgesToProcess;
    }
}
