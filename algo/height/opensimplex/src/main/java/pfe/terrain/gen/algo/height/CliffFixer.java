package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Face;

import java.util.HashSet;
import java.util.Set;

public class CliffFixer {

    public void fixBorderCliffs(IslandMap islandMap)
            throws NoSuchKeyException, KeyTypeMismatch {
        Set<Face> borders = new HashSet<>();
        for (Face face : islandMap.getFaces()) {
            if (face.getProperty(OpenSimplexHeight.faceBorderKey).value) {
                borders.add(face);
            }
        }

    }

    private boolean faceIsCliff(Face face) {
        return false;
    }


    private void smoothCliff(Face start, int left) {

    }

}
