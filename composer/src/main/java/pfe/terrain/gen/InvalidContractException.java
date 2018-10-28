package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Contract;

public class InvalidContractException extends Exception {

    public InvalidContractException(Contract contract) {
        super("The contract " + contract + " is invalid");
    }

}
