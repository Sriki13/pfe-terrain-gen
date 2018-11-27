package pfe.terrain.gen.algo.constraints;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.Param;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.EdgeSet;
import pfe.terrain.gen.algo.geometry.FaceSet;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Contract implements Parameters {

    public static Set<Key> asKeySet(Key... params) {
        return Stream.of(params).collect(Collectors.toSet());
    }

    public static Set<Param> asParamSet(Param... params) {
        return Stream.of(params).collect(Collectors.toSet());
    }

    public static final String verticesPrefix = "VERTICES_";
    public static final String edgesPrefix = "EDGES_";
    public static final String facesPrefix = "FACES_";

    public static final Key<CoordSet> vertices = new Key<>("VERTICES", CoordSet.class);
    public static final Key<EdgeSet> edges = new Key<>("EDGES", EdgeSet.class);
    public static final Key<FaceSet> faces = new Key<>("FACES", FaceSet.class);
    public static final Key<Integer> size = new Key<>("SIZE", Integer.class);
    public static final Key<Integer> seed = new Key<>("SEED", Integer.class);

    public abstract Constraints getContract();

    public void debugExecute(IslandMap map, Context context) throws NoSuchKeyException, InvalidAlgorithmParameters, KeyTypeMismatch, DuplicateKeyException {
        String algorithmName = this.getClass().getSimpleName();
        Logger logger = Logger.getLogger(algorithmName);
        String titleCard = "-------------------------";
        logger.info(titleCard + " Executing algorithm " + algorithmName + " " + titleCard);
        long startTime = System.nanoTime();
        execute(map, context);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000;
        logger.info("Done executing algorithm " + algorithmName + " in " + duration + " microseconds");
        logger.info("\nVerifying contract...");
        for (Key key : getContract().getCreated()) {
            logger.info("Verifying presence of key : " + key);
            if (!(map.assertContaining(key))) {
                logger.log(Level.SEVERE, "Unrespected contract for " + algorithmName);
                throw new NoSuchKeyException(key.getId());
            }
            logger.info(key.toString() + " is set");
        }
        logger.info(titleCard + " Execution and Verification of " + algorithmName + " done " + titleCard + "\n\n");
    }


    public abstract void execute(IslandMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch;

    public String getName() {
        return getClass().getSimpleName();
    }

}
