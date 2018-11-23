package pfe.terrain.gen.water;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.WaterKind;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.HashSet;
import java.util.Set;

public class WaterFromHeight extends WaterFromHeightGenerator {



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
                    || vertex.getProperty(heightKey).value > 0) {
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
            face.putProperty(waterKindKey, WaterKind.OCEAN);
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
