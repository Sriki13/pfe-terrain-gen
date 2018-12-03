package pfe.terrain.gen.water;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.key.Key;
import pfe.terrain.gen.algo.key.Param;
import pfe.terrain.gen.algo.key.SerializableKey;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Random;
import java.util.Set;

public class RadialWaterGeneration extends Contract {

    static final Param<Double> islandSizeParam = Param.generateDefaultDoubleParam("islandSize",
            "Size of the island, 0.0 will yield a very small island, 1.0 will create a big island", 1.0,
            "Island size");
    static final Param<Double> islandScatterParam = Param.generateDefaultDoubleParam("islandScatter",
            "Rate of scattering, 0.0 will yield a full island, 1.0 will create an archipelago with ridges", 0.0,
            "Terrain scatter");

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
        return asParamSet(islandScatterParam, islandSizeParam);
    }

    @Override
    public void execute(IslandMap map, Context context) {
        double islandSize = context.getParamOrDefault(islandSizeParam);
        double factor = context.getParamOrDefault(islandScatterParam);
        RadialShape shape = new RadialShape(islandSize, (factor * 4) + 1, new Random(map.getSeed()));
        int size = map.getSize();
        for (Face face : map.getFaces()) {
            BooleanType isWater = new BooleanType(shape.isWater(2 * (face.getCenter().x / size - 0.5), 2 * (face.getCenter().y / size - 0.5)));
            face.putProperty(faceWaterKey, isWater);
            if (isWater.value) {
                face.putProperty(waterKindKey, WaterKind.OCEAN);
            } else {
                face.putProperty(waterKindKey, WaterKind.NONE);
            }
            face.getCenter().putProperty(vertexWaterKey, isWater);
            for (Coord coord : face.getBorderVertices()) {
                try {
                    coord.getProperty(vertexWaterKey);
                    if (isWater.value) {
                        coord.putProperty(vertexWaterKey, isWater);
                    }
                } catch (NoSuchKeyException ke) {
                    coord.putProperty(vertexWaterKey, isWater);
                }
            }
        }
    }
}
