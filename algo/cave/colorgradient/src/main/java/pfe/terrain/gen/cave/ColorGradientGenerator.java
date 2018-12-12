package pfe.terrain.gen.cave;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.IntegerType;

import java.awt.*;

public class ColorGradientGenerator extends Contract {


    private static final Key<BooleanType> FACE_WALL_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    private static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "CAVE_HEIGHT", "height", DoubleType.class);

    private static final Key<IntegerType> GRADIENT_KEY =
            new SerializableKey<>(FACES_PREFIX + "COLOR", "color", IntegerType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, FACE_WALL_KEY, HEIGHT_KEY),
                asKeySet(GRADIENT_KEY));
    }

    @Override
    public String getDescription() {
        return "Assign color to faces";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        for (Face face : map.getProperty(FACES)) {
            if (face.getProperty(FACE_WALL_KEY).value) {
                face.putProperty(GRADIENT_KEY, new IntegerType(4210237));
            } else {
                double height = face.getCenter().getProperty(HEIGHT_KEY).value;
                if (height > 0) {
                    face.putProperty(GRADIENT_KEY, new IntegerType(getRgb(20, 36, height)));
                } else {
                    face.putProperty(GRADIENT_KEY, new IntegerType(getRgb(218, 19, height)));
                }
            }
        }
    }

    public int getRgb(int h, int s, double height) {
        float b = (float) ((1.5 * height) + 75) / 255f;
        if (b < 0.05f) {
            b = 0.05f;
        } else if (b > 0.8f) {
            b = 0.8f;
        }
        return Color.getHSBColor(h / 255f, s / 255f, b).getRGB();
    }
}