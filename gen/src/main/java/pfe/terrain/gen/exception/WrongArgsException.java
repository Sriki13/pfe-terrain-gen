package pfe.terrain.gen.exception;

public class WrongArgsException extends Exception {

    public WrongArgsException(String message){super(message);}
    public WrongArgsException(){super("Missing args");}
}
