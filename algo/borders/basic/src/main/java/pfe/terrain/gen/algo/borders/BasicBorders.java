package pfe.terrain.gen.algo.borders;

import com.vividsolutions.jts.geom.Coordinate;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.algorithms.BordersGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.BordersSet;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;

import java.util.Set;
import java.util.stream.Collectors;

public class BasicBorders implements BordersGenerator {

    @Override
    public void execute(IslandMap islandMap)
            throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        CoordSet coords = islandMap.getProperty(new Key<>("VERTICES", CoordSet.class));
        Set<Coordinate> borderVertices = coords.stream()
                .filter(c -> isBorder(c, islandMap.getSize()))
                .collect(Collectors.toSet());
        FaceSet faces = islandMap.getProperty(new Key<>("FACES", FaceSet.class));
        Set<Face> borderFaces = faces.stream()
                .filter(f -> isBorder(f, islandMap.getSize()))
                .collect(Collectors.toSet());
        islandMap.putProperty(new Key<>("BORDERS", BordersSet.class), new BordersSet(borderVertices, borderFaces));
    }

    @Override
    public String getName() {
        return "Basic Borders";
    }

    private boolean isBorder(Coordinate coord, int islandSize) {
        return !inBounds(coord.x, islandSize) || !inBounds(coord.y, islandSize);
    }

    private boolean inBounds(double coord, int islandSize) {
        return coord > 0 && coord < islandSize;
    }

    private boolean isBorder(Face face, int islandSize) {
        for (Coordinate coord : face.getVertices()) {
            if (isBorder(coord, islandSize)) {
                return true;
            }
        }
        return false;
    }

}
