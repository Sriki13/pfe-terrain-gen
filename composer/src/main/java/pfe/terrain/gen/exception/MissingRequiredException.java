package pfe.terrain.gen.exception;

public class MissingRequiredException extends Exception {

    public MissingRequiredException(String element) {
        super("A required elements is missing: " + element);
    }
}
