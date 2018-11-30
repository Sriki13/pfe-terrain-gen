package pfe.terrain.factory.exception;

public class NoSuchAlgorithmException extends Exception {
    public NoSuchAlgorithmException(String name){
        super("missing algorithm : " + name);
    }
}
