package pfe.terrain.gen.algo.borders;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.geometry.FaceSet;
import pfe.terrain.gen.algo.island.Biome;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.key.Key;
import pfe.terrain.gen.algo.key.SerializableKey;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.TreeType;

public class RandomTreesByBiome extends Contract {


    static final Key<Biome> faceBiomeKey =
            new SerializableKey<>(facesPrefix + "BIOME", "biome", Biome.class);

    static final SerializableKey<DoubleType> facePitchKey =
            new SerializableKey<>(facesPrefix + "HAS_PITCH", "pitch", DoubleType.class);

    static final SerializableKey<TreeType> treesKey =
            new SerializableKey<>(facesPrefix + "HAS_PITCH", "trees", TreeType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(faces, faceBiomeKey, facePitchKey),
                asKeySet(treesKey)
        );
    }

    @Override
    public void execute(IslandMap islandMap, Context context) {
        FaceSet faces = islandMap.getFaces();
    }
}
