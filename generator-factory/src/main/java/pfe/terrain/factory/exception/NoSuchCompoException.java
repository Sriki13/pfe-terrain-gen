package pfe.terrain.factory.exception;

public class NoSuchCompoException extends Exception {
    public NoSuchCompoException(){
        super("specified composition is not in the system");
    }
}
