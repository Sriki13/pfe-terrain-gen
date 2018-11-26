package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.parsing.OrderedContract;
import pfe.terrain.gen.exception.MissingContractException;

import java.util.Arrays;

public class MapGeneratorTest {

    private MapGenerator map;

    @Before
    public void init(){
        this.map = new MapGenerator(Arrays.asList(new Contract() {
            @Override
            public Constraints getContract() {
                return null;
            }

            @Override
            public void execute(IslandMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

            }
        }));
    }

    @Test(expected = MissingContractException.class)
    public void contractListTest() throws Exception{
        this.map.execute(new OrderedContract("zezez",12));
    }
}
