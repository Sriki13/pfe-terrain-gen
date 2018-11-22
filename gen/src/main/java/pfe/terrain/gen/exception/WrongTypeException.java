package pfe.terrain.gen.exception;

public class WrongTypeException extends Exception {
    public WrongTypeException(){
        super("types are not matching");
    }
}
