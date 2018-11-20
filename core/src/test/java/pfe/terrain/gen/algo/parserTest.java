package pfe.terrain.gen.algo;

import org.junit.Assert;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.*;
import pfe.terrain.gen.algo.parsing.OrderParser;
import pfe.terrain.gen.algo.parsing.OrderedContract;

import java.util.Arrays;
import java.util.List;

public class parserTest {
    private OrderParser parser = new OrderParser();


    @Test
    public void toJson() throws Exception{
        Contract first = new Contract() {
            @Override
            public Constraints getContract() {
                return null;
            }

            @Override
            public void execute(IslandMap map) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

            }

            @Override
            public String getName() {
                return "TEST1";
            }
        };

        Contract second = new Contract() {
            @Override
            public Constraints getContract() {
                return null;
            }

            @Override
            public void execute(IslandMap map) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

            }

            @Override
            public String getName() {
                return "TEST2";
            }
        };

        String json = parser.writeList(Arrays.asList(first,second));

        System.out.println(json);

        List<OrderedContract> contracts = parser.getList(json);


        Assert.assertEquals(2,contracts.size());
        Assert.assertEquals(first.getName(),contracts.get(0).getName());
        Assert.assertEquals(second.getName(),contracts.get(1).getName());
    }

    @Test(expected = OrderParsingException.class)
    public void wrongFormatException() throws Exception{
        parser.getList("azeaze");
    }
}
