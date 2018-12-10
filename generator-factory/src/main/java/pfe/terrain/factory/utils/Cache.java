package pfe.terrain.factory.utils;

import java.util.Objects;

public class Cache<T> {

    private boolean isPresent;
    private T val;
    private long lastFetch;
    private long timeout;

    private Fetcher<T> fetcher;

    public Cache(Fetcher<T> fetcher, long timeout){
        this.fetcher = fetcher;
        this.isPresent = false;
        this.timeout = timeout;
    }

    public T get() throws Exception{
        if(!isPresent || System.currentTimeMillis() - lastFetch > timeout){
            return this.fetch();
        }

        return this.val;




    }

    private T fetch() throws Exception{
        this.val = fetcher.fetch();
        this.isPresent = true;
        this.lastFetch = System.currentTimeMillis();
        return this.val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cache<?> cache = (Cache<?>) o;
        return timeout == cache.timeout &&
                Objects.equals(fetcher, cache.fetcher);
    }

    @Override
    public int hashCode() {

        return Objects.hash(timeout, fetcher);
    }
}
