package pfe.terrain.gen.exception;

public class MissingRequiredException extends Exception {

    public MissingRequiredException(){
        super("A required elements is missing");
    }
}
