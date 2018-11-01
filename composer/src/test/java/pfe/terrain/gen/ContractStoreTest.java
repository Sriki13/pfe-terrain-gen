package pfe.terrain.gen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.exception.MissingRequiredException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContractStoreTest {
    private ContractStore store;

    private Set<String> expectedRequired;
    private Set<String> expectedCreated;

    @Before
    public void init(){
        List<Contract> contracts = Arrays.asList(
                new TestContract("1",Arrays.asList("EDGE","MESH"),Arrays.asList("POINT")),
                new TestContract("2",Arrays.asList("POINT","LINE"),Arrays.asList()),
                new TestContract("3",Arrays.asList("RECTANGLE"),Arrays.asList("POINT","LINE"))
        );
        this.store = new ContractStore(contracts);

        this.expectedRequired = new HashSet<>();
        expectedRequired.add("POINT");
        expectedRequired.add("LINE");

        this.expectedCreated = new HashSet<>();
        expectedCreated.add("POINT");
        expectedCreated.add("LINE");
        expectedCreated.add("EDGE");
        expectedCreated.add("RECTANGLE");
        expectedCreated.add("MESH");
    }

    @Test
    public void requiredTest(){
        Assert.assertArrayEquals(expectedRequired.toArray(),store.getAllRequired().toArray());
    }

    @Test
    public void createdTest(){
        Assert.assertArrayEquals(expectedCreated.toArray(),store.getAllCreated().toArray());
    }

    @Test
    public void addTest(){
        this.store.add(new TestContract("new",Arrays.asList("CIRCLE"),Arrays.asList("SQUARE")));

        this.expectedCreated.add("CIRCLE");
        this.expectedRequired.add("SQUARE");

        Assert.assertArrayEquals(expectedRequired.toArray(),store.getAllRequired().toArray());
        Assert.assertArrayEquals(expectedCreated.toArray(),store.getAllCreated().toArray());
    }

    @Test
    public void getElementTest() throws Exception{
        Contract contract = this.store.getContractCreating("MESH");
        Assert.assertNotEquals(null,contract);
    }

    @Test(expected = MissingRequiredException.class)
    public void noElementTest() throws Exception{
        Contract contract = this.store.getContractCreating("WOLA");

    }
}
