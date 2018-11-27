package pfe.terrain.generatorService.holder;

import pfe.terrain.gen.algo.Key;

public class Parameter {
    private Key key;
    private String contractName;
    private String description;

    public Parameter(Key key, String contractName, String description) {
        this.key = key;
        this.contractName = contractName;
        this.description = description;
    }

    public Key getKey() {
        return key;
    }

    public String getContractName() {
        return contractName;
    }

    public String getDescription() {
        return description;
    }
}
