package pfe.terrain.gen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.EdgeSet;
import pfe.terrain.gen.algo.geometry.FaceSet;
import pfe.terrain.gen.exception.MissingRequiredException;

import java.util.*;

public class ContractStoreTest {
    private ContractStore store;

    private Set<Key> expectedRequired;
    private Set<Key> expectedCreated;

    @Before
    public void init() {
        List<Contract> contracts = Arrays.asList(
                new TestContract("1", Arrays.asList(new Key<>("EDGES", EdgeSet.class), new Key<>("MESH", FaceSet.class)),
                        Collections.singletonList(new Key<>("POINTS", CoordSet.class))),
                new TestContract("2", Arrays.asList(new Key<>("POINTS", CoordSet.class), new Key<>("LINE", String.class)),
                        Collections.emptyList()),
                new TestContract("3", Collections.singletonList(new Key<>("RECTANGLE", Integer.class)),
                        Arrays.asList(new Key<>("POINTS", CoordSet.class), new Key<>("LINE", String.class))
                )
        );
        this.store = new ContractStore(contracts);

        this.expectedRequired = new HashSet<>();
        expectedRequired.add(new Key<>("POINTS", CoordSet.class));
        expectedRequired.add(new Key<>("LINE", String.class));

        this.expectedCreated = new HashSet<>();
        expectedCreated.add(new Key<>("POINTS", CoordSet.class));
        expectedCreated.add(new Key<>("LINE", String.class));
        expectedCreated.add(new Key<>("EDGES", EdgeSet.class));
        expectedCreated.add(new Key<>("RECTANGLE", Integer.class));
        expectedCreated.add(new Key<>("MESH", FaceSet.class));
    }

    @Test
    public void requiredTest() {
        Assert.assertEquals(expectedRequired, store.getAllRequired());
    }

    @Test
    public void createdTest() {
        Assert.assertEquals(expectedCreated, store.getAllCreated());
    }

    @Test
    public void addTest() {
        this.store.add(new TestContract("new", Collections.singletonList(new Key<>("CIRCLE", Boolean.class)), Arrays.asList(new Key<>("SQUARE", Double.class))));

        this.expectedCreated.add(new Key<>("CIRCLE", Boolean.class));
        this.expectedRequired.add(new Key<>("SQUARE", Double.class));

        Assert.assertEquals(expectedRequired, store.getAllRequired());
        Assert.assertEquals(expectedCreated, store.getAllCreated());
    }

    @Test
    public void getElementTest() throws Exception {
        Contract contract = this.store.getContractCreating(new Key<>("MESH", FaceSet.class));
        Assert.assertNotEquals(null, contract);
    }

    @Test(expected = MissingRequiredException.class)
    public void noElementTest() throws Exception {
        Contract contract = this.store.getContractCreating(new Key<>("WOLA", Vector.class));
    }
}
