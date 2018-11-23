package pfe.terrain.gen.exception;

public class MissingContractException extends Exception {
    public MissingContractException(String contractName){
        super(contractName + "is not found in env and cannot be run");
    }
}
