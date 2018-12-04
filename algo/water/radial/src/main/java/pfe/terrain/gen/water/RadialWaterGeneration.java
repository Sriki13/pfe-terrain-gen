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

import java.util.Random;
import java.util.Set;

public class RadialWaterGeneration extends Contract {

    static final Param<Double> ISLAND_SIZE_PARAM = Param.generateDefaultDoubleParam("islandSize",
            "Size of the island, 0.0 will yield a very small island, 1.0 will create a big island", 1.0,
            "Island size");
    static final Param<Double> ISLAND_SCATTER_PARAM = Param.generateDefaultDoubleParam("islandScatter",
            "Rate of scattering, 0.0 will yield a full island, 1.0 will create an archipelago with ridges", 0.0,
            "Terrain scatter");

    static final Key<BooleanType> FACE_WATER_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    static final Key<BooleanType> VERTEX_WATER_KEY =
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
        return asParamSet(ISLAND_SCATTER_PARAM, ISLAND_SIZE_PARAM);
    }

    @Override
    public void execute(IslandMap map, Context context) {
        double islandSize = context.getParamOrDefault(ISLAND_SIZE_PARAM);
        double factor = context.getParamOrDefault(ISLAND_SCATTER_PARAM);
        RadialShape shape = new RadialShape(islandSize, (factor * 4) + 1, new Random(map.getSeed()));
        int size = map.getSize();
        for (Face face : map.getFaces()) {
            BooleanType isWater = new BooleanType(shape.isWater(2 * (face.getCenter().x / size - 0.5), 2 * (face.getCenter().y / size - 0.5)));
            face.putProperty(FACE_WATER_KEY, isWater);
            if (isWater.value) {
                face.putProperty(WATER_KIND_KEY, WaterKind.OCEAN);
            } else {
                face.putProperty(WATER_KIND_KEY, WaterKind.NONE);
            }
            face.getCenter().putProperty(VERTEX_WATER_KEY, isWater);
            //noinspection Duplicates
            for (Coord coord : face.getBorderVertices()) {
                try {
                    coord.getProperty(VERTEX_WATER_KEY);
                    if (isWater.value) {
                        coord.putProperty(VERTEX_WATER_KEY, isWater);
                    }
                } catch (NoSuchKeyException ke) {
                    coord.putProperty(VERTEX_WATER_KEY, isWater);
                }
            }
        }
    }
}
