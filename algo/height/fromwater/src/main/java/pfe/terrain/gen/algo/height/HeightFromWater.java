package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.*;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class HeightFromWater extends Contract {

    private final Param<Double> hardnessParam = new Param<>("hardness", Double.class,
            "0-1", "Defines the elevation of the island, 0 = almost flat, 1 = cliffy", 0.5);

    public static final Key<DoubleType> vertexHeightKey =
            new SerializableKey<>(verticesPrefix + "HEIGHT", "height", DoubleType.class);

    public static final Key<BooleanType> vertexWaterKey = new Key<>(verticesPrefix + "IS_WATER", BooleanType.class);

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(hardnessParam);
    }

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(seed, faces, edges, vertices, vertexWaterKey),
                asKeySet(vertexHeightKey)
        );
    }

    @Override
    public void execute(IslandMap map, Context context) {
        double hardness = context.getParamOrDefault(hardnessParam);
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
        Random random = new Random(map.getSeed());

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
            height += (random.nextDouble() + 0.1) / 4 + 5 * hardness;
        }

        // Set center as mean height to conform
        for (Face face : map.getFaces()) {
            double sum = 0;
            int total = 0;
            for (Coord border : face.getBorderVertices()) {
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
