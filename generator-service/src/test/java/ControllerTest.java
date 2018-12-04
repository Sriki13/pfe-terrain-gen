import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.gen.algo.Generator;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.generatorService.controller.ServiceController;
import pfe.terrain.generatorService.holder.Algorithm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@Ignore
public class ControllerTest {
    private ServiceController controller;

    private static final Param<Integer> salut = new Param<>("salut", Integer.class, "", "", 1, "");

    private class TestContract extends Contract {

        @Override
        public Set<Param> getRequestedParameters() {
            return asParamSet(salut);
        }

        @Override
        public Constraints getContract() {
            return new Constraints(new HashSet<>(), new HashSet<>());
        }

        @Override
        public void execute(IslandMap map, Context context) {
        }

        @Override
        public String getName() {
            return "Test";
        }

    }

    private class TestGenerator implements Generator{
        @Override
        public String generate() {
            return "salut";
        }

        @Override
        public void setParams(Context map) {

        }

        @Override
        public List<Contract> getContracts() {
            return Arrays.asList(new TestContract());
        }
    }

    @Before
    public void init() throws Exception {
        controller = new ServiceController(new TestGenerator());
    }

    @Test
    public void setContextTest() throws Exception {
        controller.setContext("{\"salut\" : 12}");

        Context context = controller.getContext();

        assertEquals(new Integer(12), context.getParamOrDefault(salut));
    }

    @Test
    public void setContextWithUnknownKeyTest() throws Exception{
        controller.setContext("{\"eeeee\" : 12}");

        Context context = controller.getContext();

        int val = context.getParamOrDefault(new Param<>("eeeee", Integer.class, "", "", 1, ""));

        assertEquals(1,val);

    }

    @Test
    public void runWithContext() throws Exception {

        controller.setContext("{\"salut\" : 12}");

        Context context = controller.getContext();

        assertEquals(new Integer(12), context.getParamOrDefault(salut));

        String map = controller.execute();

        Assert.assertNotEquals("", map);
    }

    @Test
    public void execTest() throws Exception {
        assertEquals("salut", controller.execute());
    }

    @Test
    public void listAlgoTest() {
        List<Algorithm> algorithms = this.controller.getAlgoList();

        assertEquals(1, algorithms.size());

        assertEquals("Test", algorithms.get(0).getName());
    }


}
