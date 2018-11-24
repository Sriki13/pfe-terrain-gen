package pfe.terrain.gen.algo;

import pfe.terrain.gen.algo.exception.KeyTypeMismatch;

import java.util.logging.Logger;

public class Context extends Mappable {

    public Context() {
        super();
    }

    @Override
    public <T> void putProperty(Key<T> key, T value) {
        properties.put(key, value);
    }

    public <T> T getPropertyOrDefault(Key<T> key, T defaultValue) throws KeyTypeMismatch {
        if (properties.keySet().stream().noneMatch(cKey -> cKey.getId().equals(key.getId()))) {
            Logger.getLogger(this.getClass().getName()).info("Key : " + key + " was not found in parameters pool, defaulting to " + defaultValue);
            return defaultValue;
        }
        T value = key.getType().cast(properties.get(key));
        if (value == null) {
            Class<?> c = properties.keySet().stream().filter(cKey -> cKey.getId().equals(key.getId())).findFirst().get().getType();
            throw new KeyTypeMismatch(key.getType().toString(), c.toString());
        }
        return value;
    }
}
