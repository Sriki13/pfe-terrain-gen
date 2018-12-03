package pfe.terrain.gen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.island.IslandMap;

import java.util.Arrays;
import java.util.List;

public class MapGeneratorTest {

    private MapGenerator map;

    @Before
    public void init() {
        this.map = new MapGenerator(Arrays.asList(new Contract() {
            @Override
            public Constraints getContract() {
                return null;
            }

            @Override
            public void execute(IslandMap map, Context context) {

            }

            @Override
            public String getName() {
                return "A";
            }
        }));
    }

    @Test
    public void algoOrderTest() {
        this.map = new MapGenerator(Arrays.asList(
                new Contract() {
                    @Override
                    public Constraints getContract() {
                        return null;
                    }

                    @Override
                    public void execute(IslandMap map, Context context) {

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
                    public void execute(IslandMap map, Context context) {

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
                    public void execute(IslandMap map, Context context) {

                    }

                    @Override
                    public String getName() {
                        return "C";
                    }
                }

        ));

        List<Contract> contracts = this.map.getContracts();

        Assert.assertEquals(3, contracts.size());
        Assert.assertEquals("A", contracts.get(0).getName());
        Assert.assertEquals("B", contracts.get(1).getName());
        Assert.assertEquals("C", contracts.get(2).getName());
    }

    @Test(expected = RuntimeException.class)
    public void failTest() {
        this.map = new MapGenerator(Arrays.asList(new Contract() {
            @Override
            public Constraints getContract() {
                return null;
            }

            @Override
            public void execute(IslandMap map, Context context) {
                throw new InvalidAlgorithmParameters("salut");
            }
        }));

         this.map.generate();
    }
}
