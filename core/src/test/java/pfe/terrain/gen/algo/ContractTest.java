package pfe.terrain.gen.algo;

import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.exception.*;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.TerrainMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ContractTest {

    private class TestContract extends Contract{

        @Override
        public Constraints getContract() {
            return null;
        }

        @Override
        public void execute(TerrainMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

        }
    }

    private class TestContractB extends Contract{

        @Override
        public Constraints getContract() {
            return null;
        }

        @Override
        public void execute(TerrainMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

        }
    }

    private class SerialContract extends Contract{

        @Override
        public Constraints getContract() {
            Set<Key> required = new HashSet<>(Arrays.asList(
                    new Key<Void>("SALUT",Void.class),
                    new Key<>("TEST",Void.class)
            ));

            Set<Key> create = new HashSet<>(Arrays.asList(
                    new Key<Void>("CREATE",Void.class),
                    new Key<>("EDGE",Void.class)
            ));

            Set<Key> modif = new HashSet<>(Arrays.asList(
                    new Key<Void>("MODIF",Void.class),
                    new Key<>("SAL",Void.class)
            ));


            return new Constraints(required,create,modif);
        }

        @Override
        public void execute(TerrainMap map, Context context) {
            
        }

        @Override
        public Set<Param> getRequestedParameters() {
            Set<Param> params = new HashSet<>();

            params.add(new Param("param",Integer.class,"12,12","test",12,"salut"));

            return params;
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

    @Test
    public void toJsonTest() throws Exception{
        Contract contract = new SerialContract();
        Contract serialContract = Contract.fromJson(contract.toJson());

        assertEquals(contract.getContract().getCreated(),serialContract.getContract().getCreated());
        assertEquals(contract.getContract().getModified(),serialContract.getContract().getModified());
        assertEquals(contract.getContract().getRequired(),serialContract.getContract().getRequired());

        assertEquals(contract.getName(),serialContract.getName());

        assertEquals(contract.getRequestedParameters(),serialContract.getRequestedParameters());
    }

    @Test(expected = NotParsableContractException.class)
    public void parseException() throws Exception{
        Contract.fromJson("azeazeé");
    }

}
