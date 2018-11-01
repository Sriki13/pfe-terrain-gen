package pfe.terrain.gen.exception;

import pfe.terrain.gen.algo.constraints.Contract;

public class InvalidContractException extends Exception {

    public InvalidContractException(Contract contract) {
        super("The contract " + contract + " is invalid");
    }

}
