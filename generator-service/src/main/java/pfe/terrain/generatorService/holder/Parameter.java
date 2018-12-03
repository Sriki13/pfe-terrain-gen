package pfe.terrain.generatorService.holder;

import pfe.terrain.gen.algo.key.Key;

public class Parameter {

    private Key key;
    private String contractName;
    private String description;
    private String label;

    public Parameter(Key key, String contractName, String description, String label) {
        this.key = key;
        this.contractName = contractName;
        this.description = description;
        this.label = label;
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

    public String getLabel() {
        return label;
    }
}
