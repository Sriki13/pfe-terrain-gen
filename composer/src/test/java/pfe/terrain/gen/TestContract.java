package pfe.terrain.gen;

import pfe.terrain.gen.algo.Property;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.HashSet;
import java.util.List;

public class TestContract implements Contract {

    private String name;
    private Constraints constraints;

    public TestContract(String name, List<Property> created, List<Property> required) {
        this.name = name;
        this.constraints = new Constraints(
                new HashSet<>(required), new HashSet<>(created)
        );
    }

    @Override
    public Constraints getContract() {
        return constraints;
    }

    @Override
    public String toString() {
        return name;
    }
}
