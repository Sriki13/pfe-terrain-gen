package pfe.terrain.gen.algo.constraints;

import pfe.terrain.gen.algo.Key;

import java.util.Collections;
import java.util.Set;

public interface Parameters {
    default Set<Key> getRequestedParameters() {
        return Collections.emptySet();
    }
}
