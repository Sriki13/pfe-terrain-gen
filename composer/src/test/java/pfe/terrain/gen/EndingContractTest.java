package pfe.terrain.gen;

import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.constraints.ContractOrder.EndingContract;
import pfe.terrain.gen.exception.MultipleEnderException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EndingContractTest {

    @Test
    public void multipleEnderTest() throws Exception{
        List<Key> required = new ArrayList<>();

        required.add(DependencySolver.ALL_KEY);

        Contract contract = new TestContract("test",new ArrayList<>(),required);
        Contract contractb = new TestContract("test",new ArrayList<>(),required);

        new EndingContract(Arrays.asList(contract,contractb));
    }
}
