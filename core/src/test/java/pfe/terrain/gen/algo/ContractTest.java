package pfe.terrain.gen.algo;

import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.IslandMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ContractTest {

    private class TestContract extends Contract{

        @Override
        public Constraints getContract() {
            return null;
        }

        @Override
        public void execute(IslandMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

        }
    }

    private class TestContractB extends Contract{

        @Override
        public Constraints getContract() {
            return null;
        }

        @Override
        public void execute(IslandMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

        }
    }

    @Test
    public void equalityTest(){
        assertEquals(new TestContract(),new TestContract());

        assertEquals(new TestContract().hashCode(),new TestContract().hashCode());

        Contract contract = new TestContract();
        assertEquals(contract,contract);
        assertEquals(contract.hashCode(),contract.hashCode());
    }

    @Test
    public void diffTest(){
        assertNotEquals(new TestContract(),new TestContractB());
    }

}
