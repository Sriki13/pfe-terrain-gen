import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.generatorService.BashGenerator;
import pfe.terrain.generatorService.GeneratorLoader;
import pfe.terrain.generatorService.controller.ServiceController;
import pfe.terrain.generatorService.exception.NoSuchGenerator;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ControllerTest {
    private ServiceController controller;

    @Before
    public void init(){
        controller = new ServiceController(Arrays.asList(new Generator() {
            @Override
            public String generate() {
                return "test";
            }

            @Override
            public int getId() {
                return 0;
            }

            @Override
            public void setParams(Context map) {

            }

            @Override
            public List<Contract> getContracts() {
                return null;
            }
        }));
    }

    @Test
    public void execTest() throws Exception{
        assertEquals("test",controller.executeById(0));
    }

    @Test(expected = NoSuchGenerator.class)
    public void noSuchGenTest() throws Exception{
        controller.executeById(143);
    }

    @Test
    public void setContextTest() throws Exception{
        controller.setContext(10,"{\"salut\" : 12}");

        Context context = controller.getContextMap().get(10);

        Assert.assertEquals(12.0,context.getProperty(new Key<>("salut",Object.class)));
    }

    @Test
    public void runWithContext() throws Exception{
        File file = new File(BashGeneratorTest.class.getResource("/generator.jar").getFile());

        Generator generator = new BashGenerator(file.getCanonicalPath());

        this.controller = new ServiceController(Arrays.asList(generator));

        controller.setContext(generator.getId(),"{\"salut\" : 12}");

        Context context = controller.getContextMap().get(generator.getId());

        Assert.assertEquals(12.0,context.getProperty(new Key<>("salut",Object.class)));

        String map = controller.executeById(generator.getId());

        Assert.assertNotEquals("",map);
    }
}
