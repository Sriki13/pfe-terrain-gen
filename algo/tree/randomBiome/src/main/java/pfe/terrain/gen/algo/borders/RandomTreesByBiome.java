package pfe.terrain.gen.algo.borders;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;
import pfe.terrain.gen.algo.island.Biome;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.key.Key;
import pfe.terrain.gen.algo.key.SerializableKey;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomTreesByBiome extends Contract {


    static final Key<Biome> faceBiomeKey =
            new SerializableKey<>(facesPrefix + "BIOME", "biome", Biome.class);

    static final SerializableKey<DoubleType> facePitchKey =
            new SerializableKey<>(facesPrefix + "HAS_PITCH", "pitch", DoubleType.class);

    static final SerializableKey<TreeType> treesKey =
            new SerializableKey<>("TREES", "trees", TreeType.class);

    static final Key<DoubleType> vertexHeightKey =
            new SerializableKey<>(verticesPrefix + "HEIGHT", "height", DoubleType.class);


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(faces, faceBiomeKey, facePitchKey, seed, vertexHeightKey),
                asKeySet(treesKey)
        );
    }

    @Override
    public void execute(IslandMap islandMap, Context context) {
        FaceSet faces = islandMap.getFaces();
        Set<Coord> trees = new HashSet<>();
        Random random = new Random(islandMap.getSeed());
        for (Face face : faces) {
            Biome faceBiome = face.getProperty(faceBiomeKey);
            if (faceBiome != Biome.OCEAN && faceBiome != Biome.LAKE && faceBiome != Biome.GLACIER) {
                trees.addAll(face.getRandomPointsInside((int) (5 * faceBiome.getTreeDensity()), random));
            }
        }
        JsonCoord[] coords = new JsonCoord[trees.size()];
        int i = 0;
        for (Coord c : trees) {
            coords[i++] = new JsonCoord(c.x, c.y,0.0);
        }
        islandMap.putProperty(treesKey, new TreeType(coords));
    }
}
