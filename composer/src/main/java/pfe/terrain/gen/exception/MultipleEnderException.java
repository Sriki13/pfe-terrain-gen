package pfe.terrain.gen.exception;

import pfe.terrain.gen.algo.constraints.Contract;

public class MultipleEnderException extends Exception {
    public MultipleEnderException(Contract a , Contract b){
        super("multiple contracts are marked as ending : " + a.getName() + " and " + b.getName() );
    }
}
