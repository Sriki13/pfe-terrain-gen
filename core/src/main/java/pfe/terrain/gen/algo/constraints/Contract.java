package pfe.terrain.gen.algo.constraints;

import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.island.geometry.EdgeSet;
import pfe.terrain.gen.algo.island.geometry.FaceSet;

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

    public static final String VERTICES_PREFIX = Prefixes.VERTICES_P.getPrefix();
    public static final String EDGES_PREFIX = Prefixes.EDGES_P.getPrefix();
    public static final String FACES_PREFIX = Prefixes.FACES_P.getPrefix();

    public static final Key<CoordSet> VERTICES = new Key<>("VERTICES", CoordSet.class);
    public static final Key<EdgeSet> EDGES = new Key<>("EDGES", EdgeSet.class);
    public static final Key<FaceSet> FACES = new Key<>("FACES", FaceSet.class);
    public static final Key<Integer> SIZE = new Key<>("SIZE", Integer.class);
    public static final Key<Integer> SEED = new Key<>("SEED", Integer.class);

    public abstract Constraints getContract();

    public long debugExecute(TerrainMap map, Context context) {
        String algorithmName = this.getClass().getSimpleName();
        Logger logger = Logger.getLogger(algorithmName);
        String titleCard = "-------------------------";
        logger.info(titleCard + " Executing algorithm " + algorithmName + " " + titleCard);
        long startTime = System.nanoTime();
        execute(map, context);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        logger.info("Done executing algorithm " + algorithmName + " in " + duration / 1000 + " microseconds");
        logger.info("\nVerifying contract...");
        for (Key key : getContract().getCreated()) {
            if (key.isOptional()) {
                continue;
            }
            logger.info("Verifying presence of key : " + key);
            if (!(map.assertContaining(key))) {
                logger.log(Level.SEVERE, "Unrespected contract for " + algorithmName);
                throw new NoSuchKeyException(key.getId());
            }
            logger.info(key.toString() + " is set");
        }
        logger.info(titleCard + " Execution and Verification of " + algorithmName + " done " + titleCard + "\n\n");
        return duration;
    }

    public abstract void execute(TerrainMap map, Context context);

    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Contract)) return false;
        return this.getName().equals(((Contract) obj).getName());
    }
}
