package pfe.terrain.gen.algo.constraints;

import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;

public interface Contract {

    Constraints getContract();

    void execute(IslandMap map) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch;

    String getName();
}
