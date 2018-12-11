package pfe.terrain.gen;

import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.island.geometry.EdgeSet;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.constraints.ContractOrder.ContractOrder;
import pfe.terrain.gen.exception.DuplicatedProductionException;
import pfe.terrain.gen.exception.MissingRequiredException;
import pfe.terrain.gen.exception.MultipleEnderException;
import pfe.terrain.gen.exception.UnsolvableException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DependencySolverTest {

    private DependencySolver dependencySolver;

    // EP -> B -> A
    @Test
    public void simpleLineTree() throws Exception {
        Contract A = new TestContract("A", Collections.singletonList(new Key<>("POINTS", CoordSet.class)),
                new ArrayList<>());
        Contract B = new TestContract("B", Collections.singletonList(new Key<>("EDGES", Void.class)),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract EP = new TestContract("C", new ArrayList<>(),
                Collections.singletonList(new Key<>("EDGES", Void.class)));
        dependencySolver = new DependencySolver(Arrays.asList(A, B), new ArrayList<>(), EP);
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
        Contract B = new TestContract("B", Collections.singletonList(new Key<>("EDGES", Void.class)),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract C = new TestContract("C", Collections.singletonList(new Key<>("FACES", Void.class)),
                new ArrayList<>());
        Contract EP = new TestContract("EP", new ArrayList<>(),
                Arrays.asList(new Key<>("EDGES", Void.class), new Key<>("FACES", Void.class)));
        dependencySolver = new DependencySolver(Arrays.asList(A, B, C), new ArrayList<>(), EP);
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
        Contract B = new TestContract("B", Collections.singletonList(new Key<>("EDGES", Void.class)),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract C = new TestContract("C", Collections.singletonList(new Key<>("FACES", Void.class)),
                new ArrayList<>());
        Contract D = new TestContract("D", Collections.singletonList(new Key<>("EDGES", Void.class)),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract E = new TestContract("A", Collections.singletonList(new Key<>("POINTS", CoordSet.class)),
                new ArrayList<>());
        Contract EP = new TestContract("EP", new ArrayList<>(),
                Arrays.asList(new Key<>("EDGES", Void.class), new Key<>("FACES", Void.class)));
        dependencySolver = new DependencySolver(Arrays.asList(D, E, A, B, C), Arrays.asList(A, B), EP);
        List<Contract> got = dependencySolver.orderContracts();
        assertEquals(3, got.size());
        assertEquals(A, got.get(0));
        assertTrue(got.get(1) == B || got.get(1) == C);
        assertTrue(got.get(2) == B || got.get(2) == C);
        assertTrue(got.contains(B));
        assertTrue(got.contains(C));
    }

    @Test (expected = MissingRequiredException.class)
    public void missingResource() throws Exception{
        Contract A = new TestContract("A",new ArrayList<>(),Arrays.asList(new Key<>("EDGES",Void.class)));

        new DependencySolver(Arrays.asList(A),Arrays.asList(A),new FinalContract()).orderContracts();
    }

    @Test(expected = DuplicatedProductionException.class)
    public void sameContracts() throws Exception{
        List<Contract> contracts = new ArrayList<>();

        contracts.add(new TestContract("1",Arrays.asList(new Key<>("EDGES", EdgeSet.class)),Arrays.asList(new Key<>("VERTICES",CoordSet.class))));
        contracts.add(new TestContract("2",Arrays.asList(new Key<>("EDGES",EdgeSet.class)),Arrays.asList(new Key<>("VERTICES",CoordSet.class))));

        contracts.add(new TestContract("3",Arrays.asList(new Key<>("VERTICES",CoordSet.class)),new ArrayList<>()));
        contracts.add(new TestContract("4",Arrays.asList(new Key<>("VERTICES",CoordSet.class)),new ArrayList<>()));

        contracts.add(new TestContract("6",
                Arrays.asList(new Key<>("FACES",FaceSet.class)),
                Arrays.asList(new Key<>("EDGES", EdgeSet.class))));
        contracts.add(new TestContract("6",
                Arrays.asList(new Key<>("FACES",FaceSet.class)),
                Arrays.asList(new Key<>("EDGES", EdgeSet.class))));

        DependencySolver solver = new DependencySolver(contracts,contracts,new FinalContract());

        List<Contract> orders = solver.orderContracts();
    }

    @Test(expected = UnsolvableException.class)
    public void unsolvableTest() throws Exception{
        List<Contract> contracts = new ArrayList<>();

        contracts.add(new TestContract("1",Arrays.asList(new Key<>("EDGES", EdgeSet.class)),Arrays.asList(new Key<>("VERTICES",CoordSet.class))));
        contracts.add(new TestContract("2",Arrays.asList(new Key<>("VERTICES",CoordSet.class)),Arrays.asList(new Key<>("EDGES",EdgeSet.class))));

        new DependencySolver(contracts,contracts,
                new TestContract("3",new ArrayList<>(),Arrays.asList(new Key<>("EDGES",EdgeSet.class)))).
                orderContracts();

    }

    @Test
    public void modifyContractTest() throws Exception{
        Contract A = new TestModifyContract("A", Collections.singletonList(new Key<>("POINTS", CoordSet.class)),
                new ArrayList<>());
        Contract B = new TestModifyContract("B", Collections.singletonList(new Key<>("EDGES", Void.class)),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract C = new TestModifyContract("C", Collections.singletonList(new Key<>("FACES", Void.class)),
                new ArrayList<>());
        Contract EP = new TestContract("EP", new ArrayList<>(),
                Arrays.asList(new Key<>("EDGES", Void.class), new Key<>("FACES", Void.class)));
        dependencySolver = new DependencySolver(Arrays.asList(A, B, C), new ArrayList<>(), EP);
        List<Contract> got = dependencySolver.orderContracts();
        assertEquals(3, got.size());
        assertEquals(A, got.get(0));
        assertTrue(got.get(1) == B || got.get(1) == C);
        assertTrue(got.get(2) == B || got.get(2) == C);
        assertTrue(got.contains(B));
        assertTrue(got.contains(C));
    }

    @Test
    public void simplePriorityWithModfied() throws Exception {
        Contract A = new TestModifyContract("A", Collections.singletonList(new Key<>("POINTS", CoordSet.class)),
                new ArrayList<>());
        Contract B = new TestModifyContract("B", Collections.singletonList(new Key<>("EDGES", Void.class)),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract C = new TestModifyContract("C", Collections.singletonList(new Key<>("FACES", Void.class)),
                new ArrayList<>());
        Contract D = new TestModifyContract("D", Collections.singletonList(new Key<>("EDGES", Void.class)),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract E = new TestModifyContract("A", Collections.singletonList(new Key<>("POINTS", CoordSet.class)),
                new ArrayList<>());
        Contract EP = new TestContract("EP", new ArrayList<>(),
                Arrays.asList(new Key<>("EDGES", Void.class), new Key<>("FACES", Void.class)));
        dependencySolver = new DependencySolver(Arrays.asList(D, E, A, B, C), Arrays.asList(A, B), EP);
        List<Contract> got = dependencySolver.orderContracts();
        assertEquals(3, got.size());
        assertEquals(A, got.get(0));
        assertTrue(got.get(1) == B || got.get(1) == C);
        assertTrue(got.get(2) == B || got.get(2) == C);
        assertTrue(got.contains(B));
        assertTrue(got.contains(C));
    }

    @Test
    public void simpleDependencyTest() throws Exception{
        Contract A = new TestContract("A", Collections.singletonList(new Key<>("POINTS", CoordSet.class)),
                new ArrayList<>());
        Contract B = new TestContract("B", new ArrayList<>(),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract C = new TestContract("C", new ArrayList<>(),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract D = new TestContract("D", new ArrayList<>(),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));

        Contract EP = new TestContract("EP", new ArrayList<>(),
                new ArrayList<>());

        dependencySolver = new DependencySolver(Arrays.asList(A,B,C,D),Arrays.asList(A,B,C,D),EP);

        List<Contract> contracts = dependencySolver.orderContracts(new ContractOrder(B,C),
                new ContractOrder("C","D",Arrays.asList(A,B,C,D)));

        assertEquals(4,contracts.size());
        assertEquals(A,contracts.get(0));
        assertEquals(B,contracts.get(1));
        assertEquals(C,contracts.get(2));
        assertEquals(D,contracts.get(3));
    }

    @Test (expected = UnsolvableException.class)
    public void dependancyFailureTest() throws Exception{
        Contract A = new TestContract("A", Collections.singletonList(new Key<>("POINTS", CoordSet.class)),
                new ArrayList<>());
        Contract B = new TestContract("B", new ArrayList<>(),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract C = new TestContract("C", new ArrayList<>(),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));
        Contract D = new TestContract("D", new ArrayList<>(),
                Collections.singletonList(new Key<>("POINTS", CoordSet.class)));

        Contract EP = new TestContract("EP", new ArrayList<>(),
                new ArrayList<>());

        dependencySolver = new DependencySolver(Arrays.asList(A,B,C,D),Arrays.asList(A,B,C,D),EP);

        List<Contract> contracts = dependencySolver.orderContracts(new ContractOrder(B,C),
                new ContractOrder(C,D),
                new ContractOrder(D,A));
    }

    @Test
    public void complexModifyTest() throws Exception{
        Contract A = new TestContract("A", Collections.singletonList(new Key<>("POINTS", Void.class)),
                new ArrayList<>());
        Contract B = new TestContract("B", Arrays.asList(new Key<>("EDGE", Void.class)),
                Arrays.asList(new Key<>("POINTS", Void.class)));
        Contract C = new TestContract("C", Arrays.asList(),
                Arrays.asList(new Key<>("POINTS", Void.class)));
        Contract D = new TestContract("D", Arrays.asList(),
                Arrays.asList(new Key<>("EDGE", Void.class)),
                Arrays.asList(new Key<>("POINTS", Void.class)));

        Contract EP = new TestContract("EP", new ArrayList<>(),
                new ArrayList<>());

        dependencySolver = new DependencySolver(Arrays.asList(A,B,C,D),Arrays.asList(A,B,C,D),EP);

        List<Contract> contracts = dependencySolver.orderContracts();

        assertEquals(4,contracts.size());
        assertEquals(A,contracts.get(0));
        assertEquals(B,contracts.get(1));
        assertEquals(D,contracts.get(2));
        assertEquals(C,contracts.get(3));
    }

    @Test(expected = UnsolvableException.class)
    public void forceEnderExceptionTest() throws Exception{
        Contract A = new TestContract("A", Collections.singletonList(new Key<>("POINTS", Void.class)),
                Arrays.asList(DependencySolver.ALL_KEY));
        Contract B = new TestContract("B", Arrays.asList(new Key<>("EDGE", Void.class)),
                Arrays.asList(new Key<>("POINTS", Void.class)));
        Contract C = new TestContract("C", Arrays.asList(),
                Arrays.asList(new Key<>("POINTS", Void.class)));
        Contract D = new TestContract("D", Arrays.asList(),
                Arrays.asList(new Key<>("EDGE", Void.class)),
                Arrays.asList(new Key<>("POINTS", Void.class)));

        Contract EP = new TestContract("EP", new ArrayList<>(),
                new ArrayList<>());

        dependencySolver = new DependencySolver(Arrays.asList(A,B,C,D),Arrays.asList(A,B,C,D),EP);

        List<Contract> contracts = dependencySolver.orderContracts();
    }

    @Test (expected = MultipleEnderException.class)
    public void multipleEnderTest() throws Exception{
        Contract A = new TestContract("A", Collections.singletonList(new Key<>("POINTS", Void.class)),
                Arrays.asList(DependencySolver.ALL_KEY));
        Contract B = new TestContract("B", Arrays.asList(new Key<>("EDGE", Void.class)),
                Arrays.asList(new Key<>("POINTS", Void.class)));
        Contract C = new TestContract("C", Arrays.asList(),
                Arrays.asList(new Key<>("POINTS", Void.class)));
        Contract D = new TestContract("D", Arrays.asList(),
                Arrays.asList(new Key<>("EDGE", Void.class)),
                Arrays.asList(new Key<>("POINTS", Void.class)));
        Contract end = new TestContract("end", new ArrayList<>(),
                Arrays.asList(DependencySolver.ALL_KEY));

        Contract EP = new TestContract("EP", new ArrayList<>(),
                new ArrayList<>());

        dependencySolver = new DependencySolver(Arrays.asList(A,B,C,D,end),Arrays.asList(A,B,C,D,end),EP);

        List<Contract> contracts = dependencySolver.orderContracts();

        System.out.println(contracts);
    }

    @Test
    public void endTest() throws Exception{
        Contract A = new TestContract("A", Collections.singletonList(new Key<>("POINTS", Void.class)),
                Arrays.asList());
        Contract B = new TestContract("B", Arrays.asList(new Key<>("EDGE", Void.class)),
                Arrays.asList(new Key<>("POINTS", Void.class)));
        Contract C = new TestContract("C", Arrays.asList(),
                Arrays.asList(new Key<>("POINTS", Void.class)));
        Contract D = new TestContract("D", Arrays.asList(),
                Arrays.asList(new Key<>("EDGE", Void.class)),
                Arrays.asList(new Key<>("POINTS", Void.class)));
        Contract end = new TestContract("end", new ArrayList<>(),
                Arrays.asList(DependencySolver.ALL_KEY));

        Contract EP = new TestContract("EP", new ArrayList<>(),
                new ArrayList<>());

        dependencySolver = new DependencySolver(Arrays.asList(A,B,C,D,end),Arrays.asList(A,B,C,D,end),EP);

        List<Contract> contracts = dependencySolver.orderContracts();

        assertEquals(contracts.get(contracts.size()-1),end);

    }


}
