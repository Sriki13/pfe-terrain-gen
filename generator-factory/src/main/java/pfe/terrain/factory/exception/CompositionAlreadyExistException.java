package pfe.terrain.factory.exception;

public class CompositionAlreadyExistException extends Exception{
    public CompositionAlreadyExistException(){
        super("composition with similar name already exist");
    }
}
