package pfe.terrain.gen;

import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static pfe.terrain.gen.algo.constraints.Contract.*;

public class ModificationOrderingTest {

    static final Key<BooleanType> FACE_WALL_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    static final Key<BooleanType> VERTEX_WALL_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    @Test
    public void modifTest() throws Exception{
        Contract relaxed  = new TestContract("relaxed",
                        asKeySet(new Key<>("POINTS", CoordSet.class)),
                                asKeySet(SIZE, SEED));

        Contract mesh = new TestContract("mesh",
                asKeySet(VERTICES, EDGES, FACES),
                asKeySet(new Key<>("POINTS", CoordSet.class), SIZE)
                );

        Contract init = new TestContract("init",
                asKeySet(SEED, SIZE),
                Collections.emptySet());

        Contract noiseshape = new TestContract("noise",
                asKeySet(FACE_WALL_KEY, VERTEX_WALL_KEY),
                asKeySet(FACES, SEED));

        Contract caveWall = new TestContract("wall",
                asKeySet(HEIGHT_KEY),
                asKeySet(FACES, FACE_WALL_KEY, VERTEX_WALL_KEY));

        Contract smallCaveSuppr = new TestContract("suppr",
                asKeySet(),
                asKeySet(FACES, SEED, EDGES, VERTICES),
                asKeySet(FACE_WALL_KEY, VERTEX_WALL_KEY));

        DependencySolver solver = new DependencySolver(Arrays.asList(relaxed,mesh,init,noiseshape,caveWall,smallCaveSuppr));

        List<Contract> contracts = solver.orderContracts();

        assertEquals(noiseshape,contracts.get(3));
        assertEquals(smallCaveSuppr,contracts.get(4));
        assertEquals(caveWall,contracts.get(5));
    }
}
