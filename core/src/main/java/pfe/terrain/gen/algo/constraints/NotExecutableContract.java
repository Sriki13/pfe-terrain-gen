package pfe.terrain.gen.algo.constraints;

import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.IslandMap;

import java.util.Set;

public class NotExecutableContract extends Contract {
    private String name;
    private Set<Param> parameters;
    private Constraints constraints;

    public NotExecutableContract(String name, Set<Param> parameters, Constraints constraints) {
        this.name = name;
        this.parameters = parameters;
        this.constraints = constraints;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<Param> getRequestedParameters() {
        return this.parameters;
    }

    @Override
    public Constraints getContract() {
        return this.constraints;
    }

    @Override
    public void execute(IslandMap map, Context context) {

    }
}
