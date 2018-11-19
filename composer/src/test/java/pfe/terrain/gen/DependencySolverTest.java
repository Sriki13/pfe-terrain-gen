package pfe.terrain.gen;

import org.junit.Test;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.Edge;
import pfe.terrain.gen.algo.geometry.EdgeSet;
import pfe.terrain.gen.algo.geometry.FaceSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DependencySolverTest {

    private ChocoDependencySolver dependencySolver;

    // EP -> B -> A
    @Test
    public void simpleLineTree() throws Exception {
        Contract A = new TestContract("A", Collections.singletonList(new Key<>("POINTS", CoordSet.class)),
                new ArrayList<>());
        Contract B = new TestContract("B", Collections.singletonList(new Key<>("EDGES", EdgeSet.class)),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract EP = new TestContract("C", new ArrayList<>(),
                Collections.singletonList(new Key<>("EDGES", EdgeSet.class)));
        dependencySolver = new ChocoDependencySolver(Arrays.asList(A, B), new ArrayList<>(), EP);
        List<Contract> got = dependencySolver.orderContracts();
        assertEquals(2, got.size());
        assertEquals(A, got.get(0));
        assertEquals(B, got.get(1));
    }

    // EP -> C
    //    -> B -> A
    @Test
    public void simpleTree() throws Exception {
        Contract A = new TestContract("A", Collections.singletonList(new Key<>("POINTS", CoordSet.class)),
                new ArrayList<>());
        Contract B = new TestContract("B", Collections.singletonList(new Key<>("EDGES", EdgeSet.class)),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract C = new TestContract("C", Collections.singletonList(new Key<>("FACES", FaceSet.class)),
                new ArrayList<>());
        Contract EP = new TestContract("EP", new ArrayList<>(),
                Arrays.asList(new Key<>("EDGES", EdgeSet.class), new Key<>("FACES", FaceSet.class)));
        dependencySolver = new ChocoDependencySolver(Arrays.asList(A, B, C), new ArrayList<>(), EP);
        List<Contract> got = dependencySolver.orderContracts();
        assertEquals(3, got.size());
        assertEquals(A, got.get(0));
        assertTrue(got.get(1) == B || got.get(1) == C);
        assertTrue(got.get(2) == B || got.get(2) == C);
        assertTrue(got.contains(B));
        assertTrue(got.contains(C));
    }

    // EP -> C
    //    -> B -> A
    // + available D (like B) but we want B specifically
    // + available E (like A) but we want A specifically
    @Test
    public void simplePriority() throws Exception {
        Contract A = new TestContract("A", Collections.singletonList(new Key<>("POINTS", CoordSet.class)),
                new ArrayList<>());
        Contract B = new TestContract("B", Collections.singletonList(new Key<>("EDGES", EdgeSet.class)),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract C = new TestContract("C", Collections.singletonList(new Key<>("FACES", FaceSet.class)),
                new ArrayList<>());
        Contract D = new TestContract("D", Collections.singletonList(new Key<>("EDGES", EdgeSet.class)),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract E = new TestContract("A", Collections.singletonList(new Key<>("POINTS", CoordSet.class)),
                new ArrayList<>());
        Contract EP = new TestContract("EP", new ArrayList<>(),
                Arrays.asList(new Key<>("EDGES", EdgeSet.class), new Key<>("FACES", FaceSet.class)));
        dependencySolver = new ChocoDependencySolver(Arrays.asList(D, E, A, B, C), Arrays.asList(A, B), EP);
        List<Contract> got = dependencySolver.orderContracts();
        assertEquals(3, got.size());
        assertEquals(A, got.get(0));
        assertTrue(got.get(1) == B || got.get(1) == C);
        assertTrue(got.get(2) == B || got.get(2) == C);
        assertTrue(got.contains(B));
        assertTrue(got.contains(C));
    }


}
