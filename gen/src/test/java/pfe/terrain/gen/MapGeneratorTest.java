package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.parsing.OrderedContract;
import pfe.terrain.gen.exception.MissingContractException;

public class MapGeneratorTest {

    private MapGenerator map;

    @Before
    public void init(){
        this.map = new MapGenerator();
    }

    @Test(expected = MissingContractException.class)
    public void contractListTest() throws Exception{
        this.map.execute(new OrderedContract("zezez",12));
    }
}
