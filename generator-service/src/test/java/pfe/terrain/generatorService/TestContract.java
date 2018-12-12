package pfe.terrain.generatorService;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.TerrainMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestContract extends Contract {

    private String name;
    private Constraints constraints;
    private Set<Param> params;

    public TestContract(String name, List<Key> created, List<Key> required) {
        this.name = name;
        this.constraints = new Constraints(
                new HashSet<>(required), new HashSet<>(created)
        );
        this.params = new HashSet<>();
    }



    public TestContract(String name, List<Key> created, List<Key> required, List<Key> modified) {
        this.name = name;
        this.constraints = new Constraints(
                new HashSet<>(required), new HashSet<>(created), new HashSet<>(modified)
        );
        this.params = new HashSet<>();
    }

    public TestContract(String name, Set<Key> created, Set<Key> required) {
        this.name = name;
        this.constraints = new Constraints(
                new HashSet<>(required), new HashSet<>(created)
        );
        this.params = new HashSet<>();

    }

    public TestContract(String name, Set<Key> created, Set<Key> required, Set<Key> modified) {
        this.name = name;
        this.constraints = new Constraints(
                new HashSet<>(required), new HashSet<>(created), new HashSet<>(modified)
        );
        this.params = new HashSet<>();
    }

    public TestContract(String name, Set<Key> created, Set<Key> required,Collection<Param> params) {
        this.name = name;
        this.constraints = new Constraints(
                new HashSet<>(required), new HashSet<>(created)
        );
        this.params = new HashSet<>(params);

    }

    public TestContract(String name, Set<Key> created, Set<Key> required, Set<Key> modified, Collection<Param> params) {
        this.name = name;
        this.constraints = new Constraints(
                new HashSet<>(required), new HashSet<>(created), new HashSet<>(modified)
        );
        this.params = new HashSet<>(params);
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
    public Set<Param> getRequestedParameters() {
        return this.params;
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
