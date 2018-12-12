package pfe.terrain.factory.parser;

import pfe.terrain.factory.entities.Algorithm;

import java.util.List;

public class AlgoCompatibilityChange {
    List<String> algoNames;
    private int compateNumber;

    public AlgoCompatibilityChange(List<String> algoNames, int compateNumber) {
        this.algoNames = algoNames;
        this.compateNumber = compateNumber;
    }

    public List<String> getAlgoNames() {
        return algoNames;
    }

    public int getCompateNumber() {
        return compateNumber;
    }
}
