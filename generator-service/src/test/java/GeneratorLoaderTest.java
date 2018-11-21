import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.generatorService.GeneratorLoader;

import java.io.File;
import java.util.List;

public class GeneratorLoaderTest {

    private GeneratorLoader loader;

    @Before
    public void init(){
        loader = new GeneratorLoader();
    }

    @Test
    public void loadingTest() throws Exception{
        File file = new File(GeneratorLoaderTest.class.getResource("/").getFile());

        loader.setFolderPath(file.getCanonicalPath());

        List<Generator> gen = loader.load();

        Assert.assertNotEquals(0,gen.size());
    }

    @Test
    public void dummyJar() throws Exception{
        File file = new File(GeneratorLoaderTest.class.getResource("/dummy").getFile());

        loader.setFolderPath(file.getCanonicalPath());

        List<Generator> gen = loader.load();

        Assert.assertEquals(1,gen.size());

        Assert.assertEquals("Salut\n",gen.get(0).generate());
        Assert.assertEquals(2,gen.get(0).getId());
    }

    @Test
    public void notWorkingJar() throws Exception{
        File file = new File(GeneratorLoaderTest.class.getResource("/wrongJar").getFile());

        loader.setFolderPath(file.getCanonicalPath());

        List<Generator> gen = loader.load();

        Assert.assertEquals(0,gen.size());
    }

    @Test
    public void emptydirTest() throws Exception{
        File file = new File(GeneratorLoaderTest.class.getResource("/wrongJar").getFile());

        loader.setFolderPath(file.getCanonicalPath());

        List<Generator> gen = loader.load();

        Assert.assertEquals(0,gen.size());
    }
}
