package pfe.terrain.gen.algo.constraints;

import pfe.terrain.gen.algo.constraints.key.Key;

import java.util.HashSet;
import java.util.Set;

public class Constraints {

    private Set<Key> required;

    private Set<Key> created;

    private Set<Key> modified;


    public Constraints(Set<Key> required, Set<Key> created) {
        this.required = required;
        this.created = created;
        this.modified = new HashSet<>();
    }

    public Constraints(Set<Key> required, Set<Key> created,Set<Key> modified){
        this.required = required;
        this.created = created;
        this.modified = modified;
    }

    public Set<Key> getRequired() {
        return required;
    }

    public Set<Key> getCreated() {
        return created;
    }

    public Set<Key> getModified(){
        return this.modified;
    }
}
