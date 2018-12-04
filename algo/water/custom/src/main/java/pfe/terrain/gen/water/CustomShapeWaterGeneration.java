package pfe.terrain.gen.water;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;

public class CustomShapeWaterGeneration extends Contract {

    private static final Param<String> CUSTOM_SHAPE = new Param<>("islandShape", String.class,
            "A Square Matrix of 0 and 1 (0 water, 1 land)", "Shape of the island", "", "Island shape matrix");

    static final Param<String> PRE_MADE_SHAPE = new Param<>("premadeIslandShape", String.class,
            Arrays.toString(DefaultShape.values()), "Choose a shape of the island in presets", DefaultShape.CIRCLE.name(),
            "Island shape preset");

    static final Key<BooleanType> FACE_WATER_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    private static final Key<BooleanType> VERTEX_WATER_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    static final Key<WaterKind> WATER_KIND_KEY =
            new SerializableKey<>(FACES_PREFIX + "WATER_KIND", "waterKind", WaterKind.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, VERTICES, SEED),
                asKeySet(FACE_WATER_KEY, VERTEX_WATER_KEY, WATER_KIND_KEY)
        );
    }

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(CUSTOM_SHAPE, PRE_MADE_SHAPE);
    }

    @Override
    public void execute(IslandMap map, Context context) {
        String islandShape = context.getParamOrDefault(CUSTOM_SHAPE);
        ShapeMatrix matrix;
        if (islandShape.equals("")) {
            matrix = new ShapeMatrix(DefaultShape.valueOf(context.getParamOrDefault(PRE_MADE_SHAPE).toUpperCase()).getMatrix());
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
            face.putProperty(FACE_WATER_KEY, new BooleanType(water));
            if (water) {
                face.putProperty(WATER_KIND_KEY, WaterKind.OCEAN);
            } else {
                face.putProperty(WATER_KIND_KEY, WaterKind.NONE);
            }
            face.getCenter().putProperty(VERTEX_WATER_KEY, new BooleanType(water));
            for (Coord coord : face.getBorderVertices()) {
                try {
                    coord.getProperty(VERTEX_WATER_KEY);
                    if (water) {
                        coord.putProperty(VERTEX_WATER_KEY, new BooleanType(true));
                    }
                } catch (NoSuchKeyException ke) {
                    coord.putProperty(VERTEX_WATER_KEY, new BooleanType(water));
                }
            }
        }
    }
}