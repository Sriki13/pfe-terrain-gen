package pfe.terrain.factory.exception;

public class CannotReachRepoException extends Exception {
    public CannotReachRepoException(){
        super("cannot reach artifact repository");
    }
}
