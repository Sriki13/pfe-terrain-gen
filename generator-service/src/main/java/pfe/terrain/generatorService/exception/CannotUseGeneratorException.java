package pfe.terrain.generatorService.exception;

public class CannotUseGeneratorException extends Exception {
    public CannotUseGeneratorException(){
        super("Can't initialise generator");
    }
}
