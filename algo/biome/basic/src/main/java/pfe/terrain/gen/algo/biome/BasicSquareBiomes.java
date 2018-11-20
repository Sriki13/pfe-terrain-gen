package pfe.terrain.gen.algo.biome;

import pfe.terrain.gen.algo.Biome;
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
    public void execute(IslandMap map)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        Set<Face> borderFaces = new HashSet<>();
        for (Face face : map.getFaces()) {
            if (face.getProperty(faceBorderKey).value) {
                borderFaces.add(face);
            }
        }
        for (Face face : map.getFaces()) {
            if (borderFaces.contains(face)) {
                face.putProperty(faceBiomeKey, Biome.OCEAN);
            } else {
                face.putProperty(faceBiomeKey, Biome.SUB_TROPICAL_DESERT);
            }
        }
    }

}
