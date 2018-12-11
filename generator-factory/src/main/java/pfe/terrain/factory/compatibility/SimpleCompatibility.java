package pfe.terrain.factory.compatibility;

import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.exception.CompatibilityException;
import pfe.terrain.factory.exception.NoSuchCompatibility;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum SimpleCompatibility implements Compatibility{

    COMPATIBLE_BEWARE(0) {
        @Override
        public void check(Algorithm a, Algorithm b) throws CompatibilityException{
            Logger.getLogger("SimpleCompatibility").log(Level.WARNING,"SimpleCompatibility warning between " + a.getId() + " and " + b.getId() + " can still execute");
        }
    },
    UNCOMPATIBLE(1) {
        @Override
        public void check(Algorithm a, Algorithm b) throws CompatibilityException{
            throw new CompatibilityException(a,b);
        }
    },
    UNKNOWN(2) {
        @Override
        public void check(Algorithm a, Algorithm b)throws CompatibilityException {

        }
    };

    private int id;

    SimpleCompatibility(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public static SimpleCompatibility compatibilityFromId(int id) throws NoSuchCompatibility {
        for(SimpleCompatibility compatibility : SimpleCompatibility.values()){
            if(compatibility.getId() == id) return compatibility;
        }
        throw new NoSuchCompatibility();
    }

}
