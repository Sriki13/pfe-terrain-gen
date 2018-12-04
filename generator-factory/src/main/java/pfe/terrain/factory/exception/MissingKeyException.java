package pfe.terrain.factory.exception;

public class MissingKeyException extends Exception {
    public MissingKeyException(String keyName){
        super("Missing key : " + keyName);
    }

    public MissingKeyException(){
        super("Wrong message format");
    }
}
