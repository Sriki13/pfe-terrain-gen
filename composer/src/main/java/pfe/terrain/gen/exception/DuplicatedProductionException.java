package pfe.terrain.gen.exception;

import pfe.terrain.gen.algo.constraints.Contract;

public class DuplicatedProductionException extends Exception {
    public DuplicatedProductionException(Contract a, Contract b){
        super("Contract " + a.getName() + "and Contract " + b.getName() + " are producing the same element");
    }
}
