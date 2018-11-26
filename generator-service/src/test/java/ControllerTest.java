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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore
public class ControllerTest {
    private ServiceController controller;

    private class GeneratorContract extends Contract {

        @Override
        public Constraints getContract() {
            return new Constraints(new HashSet(),asSet(
                    new Key<>("VERTICES", CoordSet.class),
                    new Key<>("EDGES", EdgeSet.class),
                    new Key<>("FACES", FaceSet.class)));
        }

        @Override
        public void execute(IslandMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

        }
    }

    @Before
    public void init() throws Exception{
        controller = new ServiceController(Arrays.asList(new GeneratorContract()));
    }

    @Test
    public void execTest() throws Exception{
        assertEquals("test",controller.execute());
    }

    @Test
    public void setContextTest() throws Exception{
        controller.setContext("{\"salut\" : 12}");

        Context context = controller.getContext();

        Assert.assertEquals(12.0,context.getProperty(new Key<>("salut",Object.class)));
    }

    @Test
    public void runWithContext() throws Exception{



        controller.setContext("{\"salut\" : 12}");

        Context context = controller.getContext();

        Assert.assertEquals(12.0,context.getProperty(new Key<>("salut",Object.class)));

        String map = controller.execute();

        Assert.assertNotEquals("",map);
    }
}
