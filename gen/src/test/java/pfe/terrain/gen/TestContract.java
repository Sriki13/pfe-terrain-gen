package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.TerrainMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestContract extends Contract {

    private String name;
    private Constraints constraints;
    private Set<Param> params;

    @Override
    public Set<Param> getRequestedParameters() {
        return params;
    }

    public TestContract(String name, List<Key> created, List<Key> required) {
        this.name = name;
        this.constraints = new Constraints(
                new HashSet<>(required), new HashSet<>(created)
        );
    }

    public TestContract(String name, List<Key> created, List<Key> required, Set<Param> params) {
        this.name = name;
        this.params = params;
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

    public TestContract(String name, List<Key> created, List<Key> required, List<Key> modified, Set<Param> params) {
        this.name = name;
        this.params = params;
        this.constraints = new Constraints(
                new HashSet<>(required), new HashSet<>(created), new HashSet<>(modified)
        );
    }

    @Override
    public Constraints getContract() {
        return constraints;
    }

    @Override
    public String getDescription() {
        return "test contract description";
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
