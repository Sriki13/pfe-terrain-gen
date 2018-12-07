package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Contract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateStore {

    private Map<Contract, List<PropertyState>> states;

    public StateStore() {
        states = new HashMap<>();
    }


}
