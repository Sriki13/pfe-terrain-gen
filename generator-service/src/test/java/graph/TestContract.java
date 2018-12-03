package graph;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.key.Key;

import java.util.HashSet;
import java.util.List;

public class TestContract extends Contract {

    private String name;
    private Constraints constraints;

    public TestContract(String name, List<Key> created, List<Key> required) {
        this.name = name;
        this.constraints = new Constraints(
                new HashSet<>(required), new HashSet<>(created)
        );
    }

    public TestContract(String name, List<Key> created, List<Key> required, List<Key> modified) {
        this.name = name;
        this.constraints = new Constraints(
                new HashSet<>(required), new HashSet<>(created), new HashSet<>(modified)
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
