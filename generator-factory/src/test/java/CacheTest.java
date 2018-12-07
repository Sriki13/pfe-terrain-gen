import org.junit.Assert;
import org.junit.Test;
import pfe.terrain.factory.utils.Cache;
import pfe.terrain.factory.utils.Fetcher;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class CacheTest {

    private class IntFetcher implements Fetcher<Integer> {
        public int fetchCount = 0;

        @Override
        public Integer fetch() throws Exception {
            fetchCount++;
            return 3;
        }
    }


    @Test
    public void cacheTest() throws Exception{
        IntFetcher fetcher = new IntFetcher();
        Cache<Integer> cache = new Cache<>(fetcher, 200);

        assertEquals(3,cache.get().intValue());
        assertEquals(1,fetcher.fetchCount);

        TimeUnit.MILLISECONDS.sleep(50);

        assertEquals(3,cache.get().intValue());
        assertEquals(1,fetcher.fetchCount);

        TimeUnit.MILLISECONDS.sleep(201);

        assertEquals(3,cache.get().intValue());
        assertEquals(2,fetcher.fetchCount);

    }


}
