package pfe.terrain.factory;

import org.junit.Test;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.storage.AlgoStorage;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class AlgoStorageTest {
    private AlgoStorage storage = new AlgoStorage();


    @Test
    public void getAlgoListTest() throws Exception{
        List<Algorithm> algorithms = storage.getAlgoList();

        assertNotNull(algorithms);
    }
}
