package pfe.terrain.gen.algo.exception;

public class NoSuchKeyException extends Exception {
    public NoSuchKeyException(String s) {
        super("Key " + s + " does not exists");
    }
}
