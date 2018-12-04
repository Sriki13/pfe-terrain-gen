package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.OptionalKey;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.IntegerType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.HashSet;
import java.util.Set;

import static pfe.terrain.gen.algo.constraints.Contract.EDGES_PREFIX;
import static pfe.terrain.gen.algo.constraints.Contract.FACES_PREFIX;

public class AdapterUtils {

    public static final Key<DoubleType> FACE_MOISTURE =
            new SerializableKey<>(FACES_PREFIX + "HAS_MOISTURE", "moisture", DoubleType.class);

    public static final Key<MarkerType> ADAPTED_MOISTURE_KEY =
            new OptionalKey<>(FACES_PREFIX + "MOISTURE_ADAPTED", MarkerType.class);

    public static final Key<IntegerType> RIVER_FLOW_KEY =
            new OptionalKey<>(EDGES_PREFIX + "RIVER_FLOW", IntegerType.class);

    public static final Key<WaterKind> WATER_KIND_KEY =
            new Key<>(FACES_PREFIX + "WATER_KIND", WaterKind.class);

    public static final Key<BooleanType> FACE_WATER_KEY =
            new Key<>(FACES_PREFIX + "IS_WATER", BooleanType.class);

    public static final int MOISTURE_LIMIT = 1;

    public void addMoisture(Face face, double bonus) {
        double newVal = face.getProperty(FACE_MOISTURE).value + bonus;
        if (newVal > MOISTURE_LIMIT) newVal = MOISTURE_LIMIT;
        face.putProperty(FACE_MOISTURE, new DoubleType(newVal));
        face.putProperty(ADAPTED_MOISTURE_KEY, new MarkerType());
    }

    public void spreadToNeighbours(Face start, Set<Face> seen, double bonus) {
        for (Face neighbor : start.getNeighbors()) {
            if (!seen.contains(neighbor)) {
                addMoisture(neighbor, bonus);
                seen.add(neighbor);
            }
        }
    }

    public Set<Face> getTilesNextToRivers(Set<Face> allFaces) {
        Set<Face> result = new HashSet<>();
        for (Face face : allFaces) {
            for (Edge edge : face.getEdges()) {
                if (edge.hasProperty(RIVER_FLOW_KEY)) {
                    result.add(face);
                    break;
                }
            }
        }
        return result;
    }

    public Set<Face> getTilesNextToLakes(Set<Face> allFaces) {
        Set<Face> result = new HashSet<>();
        allFaces.stream()
                .filter(face -> face.getProperty(WATER_KIND_KEY) == WaterKind.LAKE)
                .forEach(face -> face.getNeighbors().forEach(neighbour -> {
                    if (!neighbour.getProperty(FACE_WATER_KEY).value) {
                        result.add(neighbour);
                    }
                }));
        return result;
    }

}
