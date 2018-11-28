package pfe.terrain.gen;

import com.flowpowered.noise.module.Module;
import com.flowpowered.noise.module.source.Billow;
import com.flowpowered.noise.module.source.RidgedMulti;
import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

public class PerlinWaterGeneration extends Contract {

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
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, KeyTypeMismatch {
        int size = map.getSize();
        Billow perlin= new Billow();
        perlin.setSeed(map.getSeed());
        for (Face face : map.getFaces()) {
            BooleanType isWater = new BooleanType(getPerlin(perlin, 2 * (face.getCenter().x / size - 0.5), 2 * (face.getCenter().y / size - 0.5), 0.1));
            face.putProperty(faceWaterKey, isWater);
            if (isWater.value) {
                face.putProperty(waterKindKey, WaterKind.OCEAN);
            } else {
                face.putProperty(waterKindKey, WaterKind.NONE);
            }
            face.getCenter().putProperty(vertexWaterKey, isWater);
            for (Coord coord : face.getBorderVertices()) {
                coord.putProperty(vertexWaterKey, isWater);
            }
        }
    }

    public boolean getPerlin(Module perlin, double x, double y, double islandSize) {
        double c = (perlin.getValue(x, y, 0)) + 0.4;
        double length = Math.abs(Math.pow(x, 2) + Math.pow(y, 2)) + 0.4;
        return c < (length);
        //return length > 1;
    }
}
