package pfe.terrain.generatorService.holder;

import pfe.terrain.gen.algo.Key;

public class Parameter {
    private Key key;
    private String contractName;

    public Parameter(Key key, String contractName) {
        this.key = key;
        this.contractName = contractName;
    }

    public Key getKey() {
        return key;
    }

    public String getContractName() {
        return contractName;
    }
}
