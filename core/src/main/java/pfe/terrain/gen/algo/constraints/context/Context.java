package pfe.terrain.gen.algo.constraints.context;

import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Context {

    private Map<Param<?>, Object> properties;

    public Context() {
        this.properties = new HashMap<>();
    }

    public <T> void putParam(Param<T> key, T value) {
        properties.put(key, value);
    }

    public <T> T getParamOrDefault(Param<T> key) {
        if (properties.keySet().stream().noneMatch(cKey -> cKey.getId().equals(key.getId()))) {
            Logger.getLogger(this.getClass().getName()).info("Key : " + key + " was not found in parameters pool, defaulting to " + key.getDefaultValue());
            return key.getDefaultValue();
        }
        T value = key.getType().cast(properties.get(key));
        if (value == null) {
            Class<?> c = properties.keySet().stream().filter(cKey -> cKey.getId().equals(key.getId())).findFirst().get().getType();
            throw new KeyTypeMismatch(key.getType().toString(), c.toString());
        }
        key.checkValue(value);
        return value;
    }

    public Map<Param<?>, Object> getProperties() {
        return properties;
    }

    public Context merge(Context toMerge) {
        Context context = new Context();

        for (Param param : this.properties.keySet()) {
            context.putParam(param, this.properties.get(param));
        }

        for (Param param : toMerge.properties.keySet()) {
            if (this.properties.containsKey(param)) continue;

            context.putParam(param, toMerge.properties.get(param));
        }

        return context;
    }
}
