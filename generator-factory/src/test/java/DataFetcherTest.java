import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.factory.extern.AlgoDataFetcher;
import pfe.terrain.factory.extern.ArtifactoryAlgoLister;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.List;

public class DataFetcherTest {
    private AlgoDataFetcher fetcher;


    @Test
    public void fetchTest() throws Exception{
        ArtifactoryAlgoLister lister = new ArtifactoryAlgoLister();
        List<Algorithm> algorithms = lister.getAlgo();

        this.fetcher = new AlgoDataFetcher(algorithms.get(0).getName());
        Contract contract = this.fetcher.getContract();
        Assert.assertNotNull(contract);
    }

    @Test (expected = CannotReachRepoException.class)
    public void cannotReachTest() throws Exception{
        this.fetcher = new AlgoDataFetcher("aezazeaze");
        this.fetcher.getContract();
    }


}
