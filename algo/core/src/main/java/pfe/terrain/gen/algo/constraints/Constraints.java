package pfe.terrain.gen.algo.constraints;

import pfe.terrain.gen.algo.Key;

import java.util.Set;

public class Constraints {

    private Set<Key> required;

    private Set<Key> created;

    public Constraints(Set<Key> required, Set<Key> created) {
        this.required = required;
        this.created = created;
    }

    public Set<Key> getRequired() {
        return required;
    }

    public Set<Key> getCreated() {
        return created;
    }
}
