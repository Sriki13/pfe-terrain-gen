package pfe.terrain.gen.water;

import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Set;
import java.util.logging.Logger;

public class CusomShapeWaterGeneration extends Contract {

    private static final String defaultShape =
            "00000000000000000000000000000000\n" +
                    "00000000000000000000000000000000\n" +
                    "00000000000000000000000000000000\n" +
                    "00000000000000000000000000000000\n" +
                    "00000000000001111110000000000000\n" +
                    "00000000001111111111110000000000\n" +
                    "00000000011111111111111000000000\n" +
                    "00000000111111111111111100000000\n" +
                    "00000001111111111111111110000000\n" +
                    "00000011111111111111111111000000\n" +
                    "00000111111111111111111111100000\n" +
                    "00000111111111111111111111100000\n" +
                    "00000111111111111111111111100000\n" +
                    "00001111111111111111111111110000\n" +
                    "00001111111111111111111111110000\n" +
                    "00001111111111111111111111110000\n" +
                    "00001111111111111111111111110000\n" +
                    "00001111111111111111111111110000\n" +
                    "00001111111111111111111111110000\n" +
                    "00001111111111111111111111100000\n" +
                    "00000111111111111111111111100000\n" +
                    "00000111111111111111111111100000\n" +
                    "00000011111111111111111111000000\n" +
                    "00000001111111111111111110000000\n" +
                    "00000001111111111111111100000000\n" +
                    "00000000011111111111111000000000\n" +
                    "00000000001111111111110000000000\n" +
                    "00000000000011111111000000000000\n" +
                    "00000000000000000000000000000000\n" +
                    "00000000000000000000000000000000\n" +
                    "00000000000000000000000000000000\n" +
                    "00000000000000000000000000000000\n";

    static final Param<String> islandShape = new Param<>("islandShape", String.class,
            "A Square Matrix of 0 and 1 (0 water, 1 land)", "Shape of the island", defaultShape);

    static final Key<BooleanType> faceWaterKey = new SerializableKey<>(facesPrefix + "IS_WATER", "isWater", BooleanType.class);
    static final Key<BooleanType> vertexWaterKey = new SerializableKey<>(verticesPrefix + "IS_WATER", "isWater", BooleanType.class);
    static final Key<WaterKind> waterKindKey = new SerializableKey<>(facesPrefix + "WATER_KIND", "waterKind", WaterKind.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(faces, vertices, seed),
                asKeySet(faceWaterKey, vertexWaterKey, waterKindKey)
        );
    }

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(islandShape);
    }

    @Override
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, KeyTypeMismatch {
        String islandSize = context.getParamOrDefault(islandShape);
        ShapeMatrix matrix = null;
        try {
            matrix = new ShapeMatrix(islandSize);
        } catch (Exception e) {
            Logger.getLogger(this.getName()).warning("Shape format is invalid, defaulting to default value");
            try {
                matrix = new ShapeMatrix(defaultShape);
            } catch (Exception e1) {
                // Should not happen
                e1.printStackTrace();
            }
        }
        int size = map.getSize();
        for (Face face : map.getFaces()) {
            Coord center = face.getCenter();
            boolean water = matrix.isWater((int) (Math.floor((center.y / size) * matrix.getSize())),
                    (int) (Math.floor((center.x / size) * matrix.getSize())));

            face.putProperty(faceWaterKey, new BooleanType(water));
            for (Coord coord : face.getBorderVertices()) {
                coord.putProperty(vertexWaterKey, new BooleanType(water));
            }
            face.getCenter().putProperty(vertexWaterKey, new BooleanType(water));
            if (water) {
                face.putProperty(waterKindKey, WaterKind.OCEAN);
            } else {
                face.putProperty(waterKindKey, WaterKind.NONE);
            }

        }
    }
}
