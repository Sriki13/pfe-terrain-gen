package pfe.terrain.gen.algo.exception;

public class NoSuchKeyException extends RuntimeException {
    public NoSuchKeyException(String s) {
        super("Key " + s + " does not exists");
    }
}
