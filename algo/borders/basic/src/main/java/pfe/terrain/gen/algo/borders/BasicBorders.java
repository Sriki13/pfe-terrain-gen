package pfe.terrain.gen.algo.borders;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.key.Key;
import pfe.terrain.gen.algo.key.SerializableKey;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.OptionalBooleanType;

import java.util.Set;
import java.util.stream.Collectors;

public class BasicBorders extends Contract {

    public final Key<BooleanType> verticeBorderKey =
            new SerializableKey<>(verticesPrefix + "IS_BORDER", "isBorder", BooleanType.class);
    public final Key<BooleanType> faceBorderKey =
            new SerializableKey<>(facesPrefix + "IS_BORDER", "isBorder", BooleanType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(vertices, edges, faces),
                asKeySet(verticeBorderKey, faceBorderKey)
        );
    }


    @Override
    public void execute(IslandMap islandMap, Context context) {
        double offset = islandMap.getSize() * 0.1;
        Set<Coord> borderVertices = islandMap.getVertices().stream()
                .filter(c -> isBorder(c, islandMap.getSize(), offset))
                .collect(Collectors.toSet());
        Set<Face> borderFaces = islandMap.getFaces().stream()
                .filter(f -> isBorder(f, islandMap.getSize(), offset))
                .collect(Collectors.toSet());
        for (Coord coord : islandMap.getVertices()) {
            coord.putProperty(verticeBorderKey, new OptionalBooleanType(borderVertices.contains(coord)));
        }
        for (Face face : islandMap.getFaces()) {
            face.putProperty(faceBorderKey, new OptionalBooleanType(borderFaces.contains(face)));
        }
    }

    private boolean isBorder(Coord coord, int islandSize, double offset) {
        return !inBounds(coord.x, islandSize, offset) || !inBounds(coord.y, islandSize, offset);
    }

    private boolean inBounds(double coord, int islandSize, double offset) {
        return coord > offset && coord < islandSize - offset;
    }

    private boolean isBorder(Face face, int islandSize, double offset) {
        for (Coord coord : face.getBorderVertices()) {
            if (isBorder(coord, islandSize, offset)) {
                return true;
            }
        }
        return false;
    }

}
