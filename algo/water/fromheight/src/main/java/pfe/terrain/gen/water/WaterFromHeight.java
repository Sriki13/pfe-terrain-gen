package pfe.terrain.gen.water;

import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class WaterFromHeight extends Contract {

    public static final Key<DoubleType> heightKey =
            new Key<>(verticesPrefix + "HEIGHT", DoubleType.class);


    public static final Key<BooleanType> faceBorderKey =
            new Key<>(facesPrefix + "IS_BORDER", BooleanType.class);

    public static final Key<BooleanType> faceWaterKey =
            new SerializableKey<>(facesPrefix + "IS_WATER", "isWater", BooleanType.class);

    public static final Key<BooleanType> vertexWaterKey =
            new SerializableKey<>(verticesPrefix + "IS_WATER", "isWater", BooleanType.class);
    public static final Key<WaterKind> waterKindKey =
            new SerializableKey<>(facesPrefix + "WATER_KIND", "waterKind", WaterKind.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(faces, vertices, heightKey),
                asKeySet(faceWaterKey, vertexWaterKey, waterKindKey)
        );
    }

    @Override
    public void execute(IslandMap map, Context context)
            throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        identifyWaterVertices(map);
        identifyWaterFaces(map);
        identifyOcean(map);
    }

    private void identifyWaterVertices(IslandMap map)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        for (Coord vertex : map.getVertices()) {
            vertex.putProperty(vertexWaterKey, new BooleanType(vertex.getProperty(heightKey).value <= 0));
        }
    }

    private void identifyWaterFaces(IslandMap map)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        for (Face face : map.getFaces()) {
            boolean isWater = true;
            for (Coord vertex : face.getBorderVertices()) {
                if (!vertex.getProperty(vertexWaterKey).value) {
                    isWater = false;
                    break;
                }
            }
            face.putProperty(faceWaterKey, new BooleanType(isWater));
            if (isWater) {
                if (face.getProperty(faceBorderKey).value) {
                    face.putProperty(waterKindKey, WaterKind.OCEAN);
                } else {
                    face.putProperty(waterKindKey, WaterKind.LAKE);
                }
            } else {
                face.putProperty(waterKindKey, WaterKind.NONE);
            }
        }
    }

    private void identifyOcean(IslandMap map)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        Set<Face> oceanFaces = new HashSet<>();
        Set<Face> borders = new HashSet<>();
        for (Face face : map.getFaces()) {
            if (face.getProperty(faceBorderKey).value) {
                borders.add(face);
            }
        }
        Set<Face> seen = new HashSet<>(borders);
        ArrayDeque<Face> queue = new ArrayDeque<>(borders);
        while (!queue.isEmpty()) {
            Face face = queue.pop();
            for (Face neighbor : face.getNeighbors()) {
                if (seen.contains(neighbor) || borders.contains(neighbor)) {
                    continue;
                }
                seen.add(neighbor);
                if (neighbor.getProperty(faceWaterKey).value) {
                    oceanFaces.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        for (Face face : oceanFaces) {
            face.putProperty(waterKindKey, WaterKind.OCEAN);
            face.putProperty(faceWaterKey, new BooleanType(true));
        }
    }

}
