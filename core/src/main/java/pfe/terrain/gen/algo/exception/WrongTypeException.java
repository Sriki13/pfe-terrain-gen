package pfe.terrain.gen.algo.exception;

public class WrongTypeException extends Exception {
    public WrongTypeException(){
        super("types are not matching");
    }
}
