package pfe.terrain.gen.algo.constraints;

import pfe.terrain.gen.algo.key.Param;

import java.util.Collections;
import java.util.Set;

public interface Parameters {
    default Set<Param> getRequestedParameters() {
        return Collections.emptySet();
    }
}
