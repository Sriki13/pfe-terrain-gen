package pfe.terrain.factory.utils;

public interface Fetcher<T> {

    T fetch() throws Exception;
}
