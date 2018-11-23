import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.generatorService.controller.ServiceController;
import pfe.terrain.generatorService.exception.NoSuchGenerator;

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
}
