package pfe.terrain.factory.compatibility;

import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.exception.CompatibilityException;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum SimpleCompatibility implements Compatibility{

    COMPATIBLE_BEWARE() {
        @Override
        public void check(Algorithm a, Algorithm b) throws CompatibilityException{
            Logger.getLogger("SimpleCompatibility").log(Level.WARNING,"SimpleCompatibility warning between " + a.getId() + " and " + b.getId() + " can still execute");
        }
    },
    UNCOMPATIBLE() {
        @Override
        public void check(Algorithm a, Algorithm b) throws CompatibilityException{
            throw new CompatibilityException(a,b);
        }
    },
    UNKNOWN() {
        @Override
        public void check(Algorithm a, Algorithm b)throws CompatibilityException {

        }
    };

}
