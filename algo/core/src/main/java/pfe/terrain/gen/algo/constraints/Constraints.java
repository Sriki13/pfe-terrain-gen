package pfe.terrain.gen.algo.constraints;

import java.util.Set;

public class Constraints {

    private Set<String> required;

    private Set<String> created;

    public Constraints(Set<String> required, Set<String> created) {
        this.required = required;
        this.created = created;
    }

    public Set<String> getRequired() {
        return required;
    }

    public Set<String> getCreated() {
        return created;
    }
}
