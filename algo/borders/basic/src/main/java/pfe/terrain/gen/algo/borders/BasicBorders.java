package pfe.terrain.gen.algo.borders;

import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.algorithms.BordersGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Set;
import java.util.stream.Collectors;

public class BasicBorders extends BordersGenerator {

    @Override
    public void execute(IslandMap islandMap) throws DuplicateKeyException {
        Set<Coord> borderVertices = islandMap.getVertices().stream()
                .filter(c -> isBorder(c, islandMap.getSize()))
                .collect(Collectors.toSet());
        Set<Face> borderFaces = islandMap.getFaces().stream()
                .filter(f -> isBorder(f, islandMap.getSize()))
                .collect(Collectors.toSet());
        for (Coord coord : islandMap.getVertices()) {
            coord.putProperty(verticeBorderKey, new BooleanType(borderVertices.contains(coord)));
        }
        for (Face face : islandMap.getFaces()) {
            face.putProperty(faceBorderKey, new BooleanType(borderFaces.contains(face)));
        }
    }

    private boolean isBorder(Coord coord, int islandSize) {
        return !inBounds(coord.x, islandSize) || !inBounds(coord.y, islandSize);
    }

    private boolean inBounds(double coord, int islandSize) {
        return coord > 0 && coord < islandSize;
    }

    private boolean isBorder(Face face, int islandSize) {
        for (Coord coord : face.getVertices()) {
            if (isBorder(coord, islandSize)) {
                return true;
            }
        }
        return false;
    }

}
