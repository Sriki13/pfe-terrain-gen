import org.junit.Assert;
import org.junit.Test;
import pfe.terrain.factory.utils.Cache;
import pfe.terrain.factory.utils.Fetcher;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

    @Test
    public void equalityWithSameFetcherTest(){
        IntFetcher fetcher = new IntFetcher();
        Cache<Integer> cache = new Cache<>(fetcher,30);
        Cache<Integer> cacheB = new Cache<>(fetcher,30);

        assertEquals(cache,cacheB);
        assertEquals(cache.hashCode(),cacheB.hashCode());

        assertNotEquals(cache,new Cache(fetcher,40));
    }

    @Test
    public void equalityWithDifferentFetcher(){
        IntFetcher fetcher = new IntFetcher();
        Cache<Integer> cache = new Cache<>(fetcher,30);
        Cache<String> cache1 = new Cache<>(new Fetcher<String>() {
            @Override
            public String fetch() throws Exception {
                return "salut";
            }
        },30);

        assertNotEquals(cache,cache1);

    }


}
