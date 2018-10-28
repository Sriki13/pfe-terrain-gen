package pfe.terrain.gen;

public class UnsolvableException extends Exception {

    public UnsolvableException() {
        super("The given configuration has no solution.");
    }

}
