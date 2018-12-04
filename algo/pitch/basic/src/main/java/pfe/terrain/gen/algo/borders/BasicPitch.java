package pfe.terrain.gen.algo.borders;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.key.Key;
import pfe.terrain.gen.algo.key.SerializableKey;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Comparator;
import java.util.Set;

public class BasicPitch extends Contract {

    static final SerializableKey<DoubleType> facePitchKey =
            new SerializableKey<>(facesPrefix + "HAS_PITCH", "pitch", DoubleType.class);

    static final Key<DoubleType> vertexHeightKey =
            new SerializableKey<>(verticesPrefix + "HEIGHT", "height", DoubleType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(faces, vertexHeightKey),
                asKeySet(facePitchKey)
        );
    }


    private final static Comparator<Coord> byHeight =
            (o1, o2) ->
                    (int) (1000 * (o1.getProperty(vertexHeightKey).value - o2.getProperty(vertexHeightKey).value));

    @Override
    public void execute(IslandMap islandMap, Context context) {
        FaceSet faces = islandMap.getFaces();
        double factor = islandMap.getSize() / 1600.0;
        for (Face face : faces) {
            face.putProperty(facePitchKey, new DoubleType(computePitch(face, factor)));
        }
    }

    double computePitch(Face face, double factor) {
        Set<Coord> borders = face.getBorderVertices();
        Coord lowest = borders.stream().min(byHeight).get();
        Coord highest = borders.stream().max(byHeight).get();
        double rise = highest.getProperty(vertexHeightKey).value - lowest.getProperty(vertexHeightKey).value;
        double run = Math.sqrt(Math.pow(highest.x - lowest.x, 2) + Math.pow(highest.y - lowest.y, 2)) * factor;
        if (run == 0) return 0;
        return 100 * (rise / run);
    }
}
