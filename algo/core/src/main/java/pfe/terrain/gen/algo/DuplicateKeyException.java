package pfe.terrain.gen.algo;

public class DuplicateKeyException extends Exception {
    public DuplicateKeyException(String s) {
        super("Key " + s + " already exists");
    }
}
