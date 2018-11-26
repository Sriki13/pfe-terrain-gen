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

import java.util.HashSet;
import java.util.Set;

public class WaterFromHeight extends Contract {

    public static final Key<DoubleType> heightKey =
            new Key<>(verticesPrefix + "HEIGHT", DoubleType.class);

    public static final Key<BooleanType> vertexBorderKey =
            new Key<>(verticesPrefix + "IS_BORDER", BooleanType.class);

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
                asSet(faces, vertices, heightKey, faceBorderKey, vertexBorderKey),
                asSet(faceWaterKey, vertexWaterKey, waterKindKey)
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
            boolean isWater = false;
            if (vertex.getProperty(vertexBorderKey).value
                    || vertex.getProperty(heightKey).value >= 0) {
                isWater = true;
            }
            vertex.putProperty(vertexWaterKey, new BooleanType(isWater));
        }
    }

    private void identifyWaterFaces(IslandMap map)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        for (Face face : map.getFaces()) {
            if (face.getProperty(faceBorderKey).value) {
                face.putProperty(faceWaterKey, new BooleanType(true));
                face.putProperty(waterKindKey, WaterKind.OCEAN);
            } else {
                boolean isWater = true;
                for (Coord vertex : face.getVertices()) {
                    if (vertex.getProperty(vertexWaterKey).value) {
                        isWater = false;
                        break;
                    }
                }
                face.putProperty(faceWaterKey, new BooleanType(isWater));
                if (isWater) {
                    face.putProperty(waterKindKey, WaterKind.LAKE);
                } else {
                    face.putProperty(waterKindKey, WaterKind.NONE);
                }
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
        for (Face face : borders) {
            analyzeNeighbors(seen, oceanFaces, face);
        }
        for (Face face : oceanFaces) {
            WaterKind kind = WaterKind.OCEAN;
            Boolean isWater = true;
            if (face.getProperty(faceBorderKey).value) {
                for (Coord vertex : face.getVertices()) {
                    if (!vertex.getProperty(vertexWaterKey).value) {
                        kind = WaterKind.NONE;
                        isWater = false;
                        break;
                    }
                }
            }
            face.putProperty(waterKindKey, kind);
            face.putProperty(faceWaterKey, new BooleanType(isWater));
        }
    }

    private void analyzeNeighbors(Set<Face> seen, Set<Face> oceanFaces, Face face)
            throws NoSuchKeyException, KeyTypeMismatch {
        for (Face neighbor : face.getNeighbors()) {
            if (seen.contains(neighbor)) {
                continue;
            }
            seen.add(neighbor);
            if (face.getProperty(faceWaterKey).value) {
                oceanFaces.add(neighbor);
                analyzeNeighbors(seen, oceanFaces, neighbor);
            }
        }
    }

}
