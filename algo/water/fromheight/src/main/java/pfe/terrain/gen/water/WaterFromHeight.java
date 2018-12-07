package pfe.terrain.gen.water;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class WaterFromHeight extends Contract {

    public static final Key<DoubleType> HEIGHT_KEY =
            new Key<>(VERTICES_PREFIX + "HEIGHT", DoubleType.class);

    public static final Key<MarkerType> FACE_BORDER_KEY =
            new Key<>(FACES_PREFIX + "IS_BORDER", MarkerType.class);

    public static final Key<BooleanType> FACE_WATER_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    public static final Key<BooleanType> VERTEX_WATER_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    public static final Key<WaterKind> WATER_KIND_KEY =
            new SerializableKey<>(FACES_PREFIX + "WATER_KIND", "waterKind", WaterKind.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, VERTICES, HEIGHT_KEY),
                asKeySet(FACE_WATER_KEY, VERTEX_WATER_KEY, WATER_KIND_KEY)
        );
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        identifyWaterVertices(map);
        identifyWaterFaces(map);
        identifyOcean(map);
    }

    private void identifyWaterVertices(TerrainMap map)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        for (Coord vertex : map.getProperty(VERTICES)) {
            vertex.putProperty(VERTEX_WATER_KEY, new BooleanType(vertex.getProperty(HEIGHT_KEY).value <= 0));
        }
    }

    private void identifyWaterFaces(TerrainMap map)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        for (Face face : map.getProperty(FACES)) {
            boolean isWater = true;
            for (Coord vertex : face.getBorderVertices()) {
                if (!vertex.getProperty(VERTEX_WATER_KEY).value) {
                    isWater = false;
                    break;
                }
            }
            face.putProperty(FACE_WATER_KEY, new BooleanType(isWater));
            if (isWater) {
                if (face.hasProperty(FACE_BORDER_KEY)) {
                    face.putProperty(WATER_KIND_KEY, WaterKind.OCEAN);
                } else {
                    face.putProperty(WATER_KIND_KEY, WaterKind.LAKE);
                }
            } else {
                face.putProperty(WATER_KIND_KEY, WaterKind.NONE);
            }
        }
    }

    private void identifyOcean(TerrainMap map)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        Set<Face> oceanFaces = new HashSet<>();
        Set<Face> borders = new HashSet<>();
        for (Face face : map.getProperty(FACES)) {
            if (face.hasProperty(FACE_BORDER_KEY)) {
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
                if (neighbor.getProperty(FACE_WATER_KEY).value) {
                    oceanFaces.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        for (Face face : oceanFaces) {
            face.putProperty(WATER_KIND_KEY, WaterKind.OCEAN);
            face.putProperty(FACE_WATER_KEY, new BooleanType(true));
        }
    }

}
