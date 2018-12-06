package pfe.terrain.gen.algo.island;

import pfe.terrain.gen.algo.Mappable;
import pfe.terrain.gen.algo.constraints.Prefixes;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;

import java.util.Collection;

public class TerrainMap extends Mappable {

    public TerrainMap() {
        super();
    }

    public <T> boolean assertContaining(Key<T> key) {
        try {
            for (Prefixes pref : Prefixes.values()) {
                if (key.getId().startsWith(pref.getPrefix())) {
                    //noinspection unchecked
                    for (Mappable mappable : (Collection<Mappable>) getProperty(pref.getKey())) {
                        mappable.getProperty(key);
                    }
                    return true;
                }
            }
            getProperty(key);
        } catch (NoSuchKeyException | KeyTypeMismatch e) {
            return false;
        }
        return true;
    }
}
