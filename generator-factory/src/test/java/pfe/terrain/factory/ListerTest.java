package pfe.terrain.factory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.factory.exception.NoArtifStringException;
import pfe.terrain.factory.extern.ArtifactoryAlgoLister;

import static org.junit.Assert.assertNotNull;

@Ignore
public class ListerTest {
    private ArtifactoryAlgoLister lister;

    @Before
    public void init(){
        lister = new ArtifactoryAlgoLister();
    }

    @Test
    public void listTest() throws Exception{
        assertNotNull(this.lister.getAlgo());
    }

    @Test(expected = CannotReachRepoException.class)
    public void cannotReachTest() throws Exception{
        ArtifactoryAlgoLister lister = new ArtifactoryAlgoLister("http://salut/artifactory/");

        lister.getAlgo();
    }

    @Test
    public void patternTest() throws Exception{
        String id = this.lister.getArtifactId("algo.salut.test/");

        Assert.assertEquals("algo.salut.test",id);
    }

    @Test(expected = NoArtifStringException.class)
    public void noIdTest() throws Exception{
        this.lister.getArtifactId("azeaze");
    }
}
