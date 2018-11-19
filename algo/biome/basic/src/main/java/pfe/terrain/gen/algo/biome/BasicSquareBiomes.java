package pfe.terrain.gen.algo.biome;

import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.algorithms.BasicBiomeGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.BordersSet;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;

import java.util.HashMap;
import java.util.Set;

public class BasicSquareBiomes implements BasicBiomeGenerator {

    @Override
    public void execute(IslandMap map)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        BordersSet borders = map.getProperty(new Key<>("BORDERS", BordersSet.class));
        Set<Face> borderFaces = borders.getBorderFaces();
        FaceSet allFaces = map.getProperty(new Key<>("FACES", FaceSet.class));
        BiomeMap biomeMap = new BiomeMap(new HashMap<>());
        for (Face face : allFaces) {
            if (borderFaces.contains(face)) {
                biomeMap.put(face, new Ocean());
            } else {
                biomeMap.put(face, new Desert());
            }
        }
        map.putProperty(new Key<>("BIOMES", BiomeMap.class), biomeMap);
    }

    @Override
    public String getName() {
        return "Basic Square Biomes";
    }

}
