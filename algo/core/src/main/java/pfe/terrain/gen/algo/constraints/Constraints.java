package pfe.terrain.gen.algo.constraints;

import pfe.terrain.gen.algo.Property;

import java.util.Set;

public class Constraints {

    private Set<Property> required;

    private Set<Property> created;

    public Constraints(Set<Property> required, Set<Property> created) {
        this.required = required;
        this.created = created;
    }

    public Set<Property> getRequired() {
        return required;
    }

    public Set<Property> getCreated() {
        return created;
    }
}
