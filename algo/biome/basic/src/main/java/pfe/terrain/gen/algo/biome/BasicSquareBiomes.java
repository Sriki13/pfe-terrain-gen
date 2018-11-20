package pfe.terrain.gen.algo.biome;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.algorithms.BasicBiomeGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Face;

import java.util.HashSet;
import java.util.Set;

public class BasicSquareBiomes extends BasicBiomeGenerator {

    @Override
    public void execute(IslandMap map, Context context)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        Set<Face> borderFaces = new HashSet<>();
        for (Face face : map.getFaces()) {
            if (face.getProperty(faceBorderKey)) {
                borderFaces.add(face);
            }
        }
        for (Face face : map.getFaces()) {
            if (borderFaces.contains(face)) {
                face.putProperty(faceBiomeKey, new Ocean());
            } else {
                face.putProperty(faceBiomeKey, new Desert());
            }
        }
    }

}
