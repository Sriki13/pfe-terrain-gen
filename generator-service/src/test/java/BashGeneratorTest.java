import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.generatorService.controller.BashGenerator;
import pfe.terrain.generatorService.exception.CannotUseGeneratorException;

import java.io.File;

public class BashGeneratorTest {

    private Generator generator;

    @Before
    public void init() throws Exception {
        File file = new File(BashGeneratorTest.class.getResource("/generator.jar").getFile());

        generator = new BashGenerator(file.getCanonicalPath());
    }

    @Test
    public void initTest(){
        Assert.assertNotNull(generator.getId());
    }

    @Test
    public void executeTest(){
        Assert.assertNotNull(generator.generate());
    }

    @Test (expected = CannotUseGeneratorException.class)
    public void initException() throws Exception{
        new BashGenerator("aezeé");
    }

}
