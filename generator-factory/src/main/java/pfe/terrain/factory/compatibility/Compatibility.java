package pfe.terrain.factory.compatibility;

import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.exception.CompatibilityException;

public interface Compatibility {
    void check(Algorithm a, Algorithm b) throws CompatibilityException;

}
