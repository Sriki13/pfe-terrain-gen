import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Param;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.generator.Generator;
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

    private static final Param<Integer> salut = new Param<>("salut", Integer.class, "", "", 1);

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
        public void execute(IslandMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        }

        @Override
        public String getName() {
            return "Test";
        }

    }

    private class TestGenerator implements Generator{
        @Override
        public String generate() throws Exception {
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

        int val = context.getParamOrDefault(new Param<>("eeeee", Integer.class, "", "", 1));

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
