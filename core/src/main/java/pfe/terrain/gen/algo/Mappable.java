package pfe.terrain.gen.algo;

import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;

import java.util.HashMap;
import java.util.Map;

public abstract class Mappable {

    protected Map<Key<?>, Object> properties;

    protected Mappable() {
        this.properties = new HashMap<>();
    }

    public <T> void putProperty(Key<T> key, T value)
            throws DuplicateKeyException {
        for (Key k : properties.keySet()) {
            if (k.getId().equals(key.getId()) && !(k.getType().equals(key.getType()))) {
                throw new DuplicateKeyException(key.getId());
            }
        }
        properties.put(key, value);
    }

    public <T> T getProperty(Key<T> key) {
        if (properties.keySet().stream().noneMatch(cKey -> cKey.getId().equals(key.getId()))) {
            if (key.isOptional()) {
                return null;
            }
            throw new NoSuchKeyException(key.getId());
        }
        T value = key.getType().cast(properties.get(key));
        if (value == null) {
            @SuppressWarnings("ConstantConditions")
            Class<?> c = properties.keySet().stream()
                    .filter(cKey -> cKey.getId().equals(key.getId()))
                    .findFirst().get().getType();
            throw new KeyTypeMismatch(key.getType().toString(), c.toString());
        }
        return value;
    }

    public <T> boolean hasProperty(Key<T> key) {
        return properties.keySet().stream().anyMatch(cKey -> cKey.getId().equals(key.getId()));
    }

    public Map<Key<?>, Object> getProperties() {
        return properties;
    }

}
