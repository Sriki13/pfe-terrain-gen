package pfe.terrain.gen.algo.borders;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Comparator;
import java.util.Set;

public class BasicPitch extends Contract {

    static final SerializableKey<DoubleType> FACE_PITCH_KEY =
            new SerializableKey<>(FACES_PREFIX + "HAS_PITCH", "pitch", DoubleType.class);

    static final Key<DoubleType> VERTEX_HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, VERTEX_HEIGHT_KEY),
                asKeySet(FACE_PITCH_KEY)
        );
    }


    private final static Comparator<Coord> BY_HEIGHT =
            (o1, o2) ->
                    (int) (1000 * (o1.getProperty(VERTEX_HEIGHT_KEY).value - o2.getProperty(VERTEX_HEIGHT_KEY).value));

    @Override
    public void execute(IslandMap islandMap, Context context) {
        FaceSet faces = islandMap.getFaces();
        double factor = 1 - islandMap.getSize() / 1600.0;
        for (Face face : faces) {
            face.putProperty(FACE_PITCH_KEY, new DoubleType(computePitch(face, factor)));
        }
    }

    @SuppressWarnings("ConstantConditions")
    double computePitch(Face face, double factor) {
        Set<Coord> borders = face.getBorderVertices();
        Coord lowest = borders.stream().min(BY_HEIGHT).get();
        Coord highest = borders.stream().max(BY_HEIGHT).get();
        double rise = highest.getProperty(VERTEX_HEIGHT_KEY).value - lowest.getProperty(VERTEX_HEIGHT_KEY).value;
        double run = Math.sqrt(Math.pow(highest.x - lowest.x, 2) + Math.pow(highest.y - lowest.y, 2));
        if (run == 0) return 0;
        return (100 * (rise / run)) / factor;
    }
}
