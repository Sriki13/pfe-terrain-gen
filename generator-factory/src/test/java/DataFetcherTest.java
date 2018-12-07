import org.junit.Assert;
import org.junit.Test;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.factory.extern.AlgoDataFetcher;
import pfe.terrain.factory.extern.ArtifactoryAlgoLister;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataFetcherTest {
    private AlgoDataFetcher fetcher;


    @Test
    public void fetchTest() throws Exception{
        ArtifactoryAlgoLister lister = new ArtifactoryAlgoLister();
        List<String> algorithms = lister.getAlgo();

        this.fetcher = new AlgoDataFetcher(algorithms.get(0));
        Algorithm contract = this.fetcher.getAlgorithm();
        Assert.assertNotNull(contract);
    }

    @Test (expected = CannotReachRepoException.class)
    public void cannotReachTest() throws Exception{
        this.fetcher = new AlgoDataFetcher("aezazeaze");
        this.fetcher.getAlgorithm();
    }

    @Test
    public void equalityTest(){
        AlgoDataFetcher fetcher = new AlgoDataFetcher("test");
        AlgoDataFetcher fetcherB = new AlgoDataFetcher("test");

        assertEquals(fetcher,fetcherB);
        assertEquals(fetcher.hashCode(),fetcherB.hashCode());

        assertNotEquals(fetcher,new AlgoDataFetcher("azeaze"));
    }


}
