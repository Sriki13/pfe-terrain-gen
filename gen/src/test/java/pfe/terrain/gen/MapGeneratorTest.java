package pfe.terrain.gen;

import org.junit.Assert;
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
import java.util.List;

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

            @Override
            public String getName() {
                return "A";
            }
        }));
    }

    @Test
    public void algoOrderTest(){
        this.map = new MapGenerator(Arrays.asList(
                new Contract() {
                    @Override
                    public Constraints getContract() {
                        return null;
                    }

                    @Override
                    public void execute(IslandMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

                    }

                    @Override
                    public String getName() {
                        return "A";
                    }
                },
                new Contract() {
                    @Override
                    public Constraints getContract() {
                        return null;
                    }

                    @Override
                    public void execute(IslandMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

                    }

                    @Override
                    public String getName() {
                        return "B";
                    }
                },
                new Contract() {
                    @Override
                    public Constraints getContract() {
                        return null;
                    }

                    @Override
                    public void execute(IslandMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

                    }

                    @Override
                    public String getName() {
                        return "C";
                    }
                }

        ));

        List<Contract> contracts = this.map.getContracts();

        Assert.assertEquals(3,contracts.size());
        Assert.assertEquals("A",contracts.get(0).getName());
        Assert.assertEquals("B",contracts.get(1).getName());
        Assert.assertEquals("C",contracts.get(2).getName());
    }

    @Test(expected = InvalidAlgorithmParameters.class)
    public void failTest() throws Exception{
        this.map = new MapGenerator(Arrays.asList(new Contract() {
            @Override
            public Constraints getContract() {
                return null;
            }

            @Override
            public void execute(IslandMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
                throw new InvalidAlgorithmParameters("salut");
            }
        }));

        this.map.generate();
    }
}
