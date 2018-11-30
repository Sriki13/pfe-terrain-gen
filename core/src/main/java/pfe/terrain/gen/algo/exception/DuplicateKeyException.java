package pfe.terrain.gen.algo.exception;

public class DuplicateKeyException extends RuntimeException {
    public DuplicateKeyException(String s) {
        super("Key " + s + " already exists");
    }
}
