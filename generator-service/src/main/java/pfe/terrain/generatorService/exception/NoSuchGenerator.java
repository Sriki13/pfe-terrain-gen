package pfe.terrain.generatorService.exception;

public class NoSuchGenerator extends Exception{
    public NoSuchGenerator(){
        super("No such generator found");
    }
}
