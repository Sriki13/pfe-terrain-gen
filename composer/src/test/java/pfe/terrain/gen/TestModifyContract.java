package pfe.terrain.gen;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.HashSet;
import java.util.List;

public class TestModifyContract extends Contract {
    private String name;
    private Constraints constraints;

    public TestModifyContract(String name, List<Key> created, List<Key> required) {
        this.name = name;
        this.constraints = new Constraints(
                new HashSet<>(), new HashSet<>(created), new HashSet<>(required)
        );
    }

    @Override
    public Constraints getContract() {
        return constraints;
    }

    @Override
    public void execute(IslandMap map, Context context) {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
