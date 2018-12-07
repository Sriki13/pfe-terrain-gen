package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.*;

public class HeightRedistribution extends Contract {

    static final Key<DoubleType> VERTEX_HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    static final Key<BooleanType> VERTEX_WATER_KEY = new Key<>(VERTICES_PREFIX + "IS_WATER", BooleanType.class);

    private Param<Double> REDISTRIBUTION_FACTOR_KEY = Param.generateDefaultDoubleParam("heightFactor",
            "How the height is distributed, for very low value there will be more high altitude points than low level one " +
            "and for medium to high value there will be a tendency to have more low level altitude points than high one", 0.5,
            "Redistribution factor");

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(VERTICES, FACES, VERTEX_WATER_KEY),
                asKeySet(),
                asKeySet(VERTEX_HEIGHT_KEY)
        );
    }

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(REDISTRIBUTION_FACTOR_KEY);
    }

    @Override
    public void execute(TerrainMap map, Context context) {

        // This code is a mess but it works

        double scaleFactor = context.getParamOrDefault(REDISTRIBUTION_FACTOR_KEY);
        scaleFactor = 1 / (0.8 + (scaleFactor * 3.2));
        Map<Coord, Double> verticesHeight = new HashMap<>();
        List<Map.Entry<Coord, Double>> orderedVertices;
        CoordSet vertices = map.getProperty(VERTICES);

        for (Coord coord : vertices) {
            if (!coord.getProperty(VERTEX_WATER_KEY).value) {
                verticesHeight.put(coord, coord.getProperty(VERTEX_HEIGHT_KEY).value);
            }
        }
        double minV = Collections.min(verticesHeight.values());
        double maxV = Collections.max(verticesHeight.values());
        verticesHeight.replaceAll((key, val) -> ((val - minV) / (maxV - minV)));
        orderedVertices = new ArrayList<>(verticesHeight.entrySet());
        orderedVertices.sort((o1, o2) -> (int) Math.ceil(1000 * (o1.getValue() - o2.getValue())));
        for (int i = 0; i < orderedVertices.size(); i++) {
            double y = i / (double) (orderedVertices.size() - 1);
            double x = Math.pow(1, scaleFactor) - Math.pow(1 - y, scaleFactor);
            orderedVertices.get(i).setValue(x);
        }
        for (Map.Entry<Coord, Double> c : orderedVertices) {
            c.setValue(c.getValue() * (maxV - minV) + minV);
        }
        for (Coord coord : vertices) {
            if (!coord.getProperty(VERTEX_WATER_KEY).value) {
                coord.putProperty(VERTEX_HEIGHT_KEY, new DoubleType(verticesHeight.get(coord)));
            }
        }
        for (Face face : map.getProperty(FACES)) {
            double sum = 0;
            int total = 0;
            for (Coord border : face.getBorderVertices()) {
                sum += border.getProperty(VERTEX_HEIGHT_KEY).value;
                total += 1;
            }
            face.getCenter().putProperty(VERTEX_HEIGHT_KEY, new DoubleType(sum / (double) total));
        }
    }
}
