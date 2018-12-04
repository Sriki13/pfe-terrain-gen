package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.EdgeSet;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.*;

public class HeightSmoothing extends Contract {

    static final Key<DoubleType> VERTEX_HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    static final Key<BooleanType> VERTEX_WATER_KEY = new Key<>(VERTICES_PREFIX + "IS_WATER", BooleanType.class);

    static final Param<Double> SMOOTHING_FACTOR = Param.generateDefaultDoubleParam("SmoothingLevel",
            "Adjust how much you want to smooth the height of the map", 0.3, "Smoothing factor");


    private Map<Coord, List<Double>> verticesHeight;


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, EDGES, VERTEX_WATER_KEY),
                asKeySet(),
                asKeySet(VERTEX_HEIGHT_KEY)
        );
    }

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(SMOOTHING_FACTOR);
    }

    @Override
    public void execute(IslandMap map, Context context) {
        EdgeSet edges = map.getEdges();
        verticesHeight = new HashMap<>();


        // Adding neighbors of coordinates to the map
        double smoothingRate = 0.1 + (context.getParamOrDefault(SMOOTHING_FACTOR) / 1.75);
        double h1;
        double h2;
        for (Edge edge : edges) {
            h1 = edge.getStart().getProperty(VERTEX_HEIGHT_KEY).value;
            h2 = edge.getEnd().getProperty(VERTEX_HEIGHT_KEY).value;
            if (!edge.getStart().getProperty(VERTEX_WATER_KEY).value) {
                putInHeightMap(edge.getStart(), h2);
            }
            if (!edge.getEnd().getProperty(VERTEX_WATER_KEY).value) {
                putInHeightMap(edge.getEnd(), h1);
            }
        }

        // Applying convolution neighborhood
        double finalHeight;
        double originHeight;
        double smoothingRateMean;
        double originRate;
        for (Map.Entry<Coord, List<Double>> entry : verticesHeight.entrySet()) {
            Coord origin = entry.getKey();
            List<Double> neighborList = entry.getValue();
            finalHeight = 0;
            originHeight = origin.getProperty(VERTEX_HEIGHT_KEY).value;
            smoothingRateMean = smoothingRate / neighborList.size();
            originRate = 1 - smoothingRate;
            for (Double neighborHeight : neighborList) {
                finalHeight += neighborHeight * smoothingRateMean;
            }
            finalHeight += originHeight * originRate;
            origin.putProperty(VERTEX_HEIGHT_KEY, new DoubleType(finalHeight));
        }

        // Adjusting faces center
        double sum;
        int total;
        for (Face face : map.getFaces()) {
            sum = 0;
            total = 0;
            for (Coord border : face.getBorderVertices()) {
                sum += border.getProperty(VERTEX_HEIGHT_KEY).value;
                total++;
            }
            face.getCenter().putProperty(VERTEX_HEIGHT_KEY, new DoubleType(sum / (double) total));
        }
    }

    private void putInHeightMap(Coord c, double height) {
        verticesHeight.computeIfAbsent(c, k -> new ArrayList<>());
        verticesHeight.get(c).add(height);
    }

}
