package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.TerrainMap;

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
    public String getDescription() {
        return null;
    }

    @Override
    public void execute(TerrainMap map, Context context) {

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
