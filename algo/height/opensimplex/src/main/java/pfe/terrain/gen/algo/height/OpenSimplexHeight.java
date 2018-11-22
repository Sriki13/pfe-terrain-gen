package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.algorithms.HeightGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;

import java.util.Set;

public class OpenSimplexHeight extends HeightGenerator {

    @Override
    public Set<Key> getRequestedParameters() {
        return null;
    }

    @Override
    public void execute(IslandMap map, Context context)
            throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        NoiseMap elevation = new NoiseMap(map.getVertices(), map.getSeed());

        elevation.addSimplexNoise(0.7, 0.05);
        elevation.addSimplexNoise(0.35, 0.025);
        elevation.addSimplexNoise(0.175, 0.0125);

        elevation.redistribute(3);
        elevation.putValuesInRange();
        elevation.ensureBordersAreLow();
        elevation.putHeightProperty();

    }
}
