package pfe.terrain.gen;

import org.junit.Test;
import pfe.terrain.gen.constraints.ContractOrder.ContractOrder;
import pfe.terrain.gen.exception.NoSuchContractException;

import java.util.ArrayList;

public class ContractOrderTest {

    @Test(expected = NoSuchContractException.class)
    public void noSuchContractTest() throws Exception{
        new ContractOrder("A","B",new ArrayList<>());
    }
}
