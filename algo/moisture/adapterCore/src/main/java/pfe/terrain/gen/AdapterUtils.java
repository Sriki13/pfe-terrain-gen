package pfe.terrain.gen;

import pfe.terrain.gen.algo.geometry.Edge;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.key.Key;
import pfe.terrain.gen.algo.key.SerializableKey;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.IntegerType;

import java.util.HashSet;
import java.util.Set;

import static pfe.terrain.gen.algo.constraints.Contract.edgesPrefix;
import static pfe.terrain.gen.algo.constraints.Contract.facesPrefix;

public class AdapterUtils {

    public static final Key<DoubleType> faceMoisture =
            new SerializableKey<>(facesPrefix + "HAS_MOISTURE", "moisture", DoubleType.class);

    public static final Key<Boolean> adaptedMoistureKey =
            new Key<>(facesPrefix + "MOISTURE_ADAPTED", Boolean.class);

    public static final Key<IntegerType> riverFlowKey =
            new Key<>(edgesPrefix + "RIVER_FLOW", IntegerType.class);

    public static final Key<WaterKind> waterKindKey =
            new Key<>(facesPrefix + "WATER_KIND", WaterKind.class);

    public static final Key<BooleanType> faceWaterKey =
            new Key<>(facesPrefix + "IS_WATER", BooleanType.class);

    public static final int MOISTURE_LIMIT = 1;

    public void setModifiedKey(Set<Face> faces) {
        faces.forEach(face -> face.putProperty(adaptedMoistureKey, false));
    }

    public void addMoisture(Face face, double bonus) {
        double newVal = face.getProperty(faceMoisture).value + bonus;
        if (newVal > MOISTURE_LIMIT) newVal = MOISTURE_LIMIT;
        face.putProperty(faceMoisture, new DoubleType(newVal));
        face.putProperty(adaptedMoistureKey, true);
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
                if (edge.getProperty(riverFlowKey).value > 0) {
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
                .filter(face -> face.getProperty(waterKindKey) == WaterKind.LAKE)
                .forEach(face -> face.getNeighbors().forEach(neighbour -> {
                    if (!neighbour.getProperty(faceWaterKey).value) {
                        result.add(neighbour);
                    }
                }));
        return result;
    }

}
