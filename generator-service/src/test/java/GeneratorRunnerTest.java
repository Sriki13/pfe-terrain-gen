import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.generatorService.GeneratorRunner;
import pfe.terrain.generatorService.exception.NoSuchGenerator;

import java.util.ArrayList;
import java.util.List;

public class GeneratorRunnerTest {

    private GeneratorRunner runner;

    private int genId = 90;

    @Before
    public void init(){
        List<Generator> generators = new ArrayList<>();

        generators.add(new Generator() {
            @Override
            public String generate() {
                return "salut";
            }

            @Override
            public int getId() {
                return genId;
            }
        });

        this.runner = new GeneratorRunner(generators);
    }


    @Test
    public void listTest(){
        Assert.assertTrue(runner.getGeneratorList().contains(String.valueOf(genId)));

    }

    @Test
    public void execTest() throws Exception{
        Assert.assertTrue(runner.executeById(genId).equals("salut"));
    }

    @Test(expected = NoSuchGenerator.class)
    public void missingGen() throws Exception{
        runner.executeById(787877788);
    }
}
