import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.gen.FinalContract;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.EdgeSet;
import pfe.terrain.gen.algo.geometry.FaceSet;
import pfe.terrain.generatorService.controller.ServiceController;
import pfe.terrain.generatorService.exception.NoSuchGenerator;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;

@Ignore
public class ControllerTest {
    private ServiceController controller;

    private class TestContract extends Contract {

        @Override
        public Set<Key> getRequestedParameters() {
            return asSet(new Key<>("salut",Integer.class));
        }

        @Override
        public Constraints getContract() {
            return new Constraints(new HashSet<>(),new HashSet<>());
        }

        @Override
        public void execute(IslandMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

        }
    }

    @Before
    public void init() throws Exception{
        controller = new ServiceController(new Generator() {
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
        });
    }

    @Test
    public void setContextTest() throws Exception{
        controller.setContext("{\"salut\" : 12}");

        Context context = controller.getContext();

        assertEquals(new Integer(12),context.getProperty(new Key<>("salut",Integer.class)));
    }

    @Test
    public void runWithContext() throws Exception{

        controller.setContext("{\"salut\" : 12}");

        Context context = controller.getContext();

        assertEquals(new Integer(12),context.getProperty(new Key<>("salut",Integer.class)));

        String map = controller.execute();

        Assert.assertNotEquals("",map);
    }

    @Test
    public void execTest(){
        assertEquals("salut",controller.execute());
    }
}
