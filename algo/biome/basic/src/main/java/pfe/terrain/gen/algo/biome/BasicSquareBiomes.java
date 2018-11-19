package pfe.terrain.gen.algo.biome;

import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.algorithms.BasicBiomeGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;

public class BasicSquareBiomes implements BasicBiomeGenerator {


    @Override
    public void execute(IslandMap map)
            throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

    }

    @Override
    public String getName() {
        return "Basic Square Biomes";
    }

}
