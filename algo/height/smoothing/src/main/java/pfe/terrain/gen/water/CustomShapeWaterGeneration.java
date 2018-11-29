package pfe.terrain.gen.water;

import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;

public class CustomShapeWaterGeneration extends Contract {

    static final Param<String> customShape = new Param<>("islandShape", String.class,
            "A Square Matrix of 0 and 1 (0 water, 1 land)", "Shape of the island", "");

    static final Param<String> premadeShape = new Param<>("premadeIslandShape", String.class,
            Arrays.toString(DefaultShape.values()), "Choose a shape of the island in presets", DefaultShape.CIRCLE.name());

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
        return asParamSet(customShape, premadeShape);
    }

    @Override
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, KeyTypeMismatch {
        String islandShape = context.getParamOrDefault(customShape);
        ShapeMatrix matrix = null;
        if (islandShape.equals("")) {
            matrix = new ShapeMatrix(DefaultShape.valueOf(context.getParamOrDefault(premadeShape)).getMatrix());
        } else {
            try {
                matrix = new ShapeMatrix(islandShape);
            } catch (Exception e) {
                Logger.getLogger(this.getName()).warning("Shape format is invalid, defaulting to default value");
                matrix = new ShapeMatrix(DefaultShape.CIRCLE.getMatrix());
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
