package pfe.terrain.gen;

import pfe.terrain.gen.algo.Mappable;
import pfe.terrain.gen.algo.constraints.key.Key;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PropertyState<T extends Mappable, V> {

    private Key<V> key;
    private Map<Mappable, V> values;

    public PropertyState(Key<V> key, Collection<Mappable> items) {
        this.key = key;
        this.values = new HashMap<>();
        for (Mappable item : items) {
            if (item.hasProperty(key)) {
                values.put(item, item.getProperty(key));
            }
        }
    }
}
