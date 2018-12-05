package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.*;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class HeightFromWater extends Contract {

    private final Param<Double> HARDNESS_PARAM = Param.generateDefaultDoubleParam("hardness",
            "Defines the elevation of the island, 0 = almost flat, 1 = with cliffs", 0.5, "Island elevation");

    public static final Key<DoubleType> VERTEX_HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    public static final Key<BooleanType> VERTEX_WATER_KEY = new Key<>(VERTICES_PREFIX + "IS_WATER", BooleanType.class);

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(HARDNESS_PARAM);
    }

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(SEED, FACES, EDGES, VERTICES, VERTEX_WATER_KEY),
                asKeySet(VERTEX_HEIGHT_KEY)
        );
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        double hardness = context.getParamOrDefault(HARDNESS_PARAM);
        Set<Coord> coordsToProcess = new HashSet<>();
        double height = 0.0;
        CoordSet vertices = map.getProperty(VERTICES);
        for (Coord vertex : vertices) {
            if (vertex.getProperty(VERTEX_WATER_KEY).value) {
                vertex.putProperty(VERTEX_HEIGHT_KEY, new DoubleType(height));
            } else {
                coordsToProcess.add(vertex);
            }
        }
        EdgeSet allEdges = new EdgeSet(map.getProperty(EDGES));
        EdgeSet edgesToProcess;
        int coordsSize = -1;
        Random random = new Random(map.getProperty(SEED));
        height += heightStep(hardness, random);

        // If coordsToProcess size wasn't reduced then only centers are remaining
        while (!(coordsSize == coordsToProcess.size())) {
            edgesToProcess = getEdgesToProcess(coordsToProcess, allEdges);
            coordsSize = coordsToProcess.size();
            for (Edge e : edgesToProcess) {
                if (coordsToProcess.contains(e.getStart())) {
                    e.getStart().putProperty(VERTEX_HEIGHT_KEY, new DoubleType(height));
                    coordsToProcess.remove(e.getStart());
                } else {
                    e.getEnd().putProperty(VERTEX_HEIGHT_KEY, new DoubleType(height));
                    coordsToProcess.remove(e.getEnd());
                }
                allEdges.remove(e);
            }
            height += heightStep(hardness, random);
        }

        // Set center as mean height to conform
        for (Face face : map.getProperty(FACES)) {
            double sum = 0;
            int total = 0;
            for (Coord border : face.getBorderVertices()) {
                sum += border.getProperty(VERTEX_HEIGHT_KEY).value;
                total++;
            }
            face.getCenter().putProperty(VERTEX_HEIGHT_KEY, new DoubleType(sum / (double) total));
        }
    }

    private double heightStep(double hardness, Random random) {
        return ((random.nextDouble() + 0.2) / 4) + 9 * hardness;
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
