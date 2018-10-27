package pfe.terrain.gen.algo;

import java.util.HashMap;
import java.util.Map;

public class IslandMap {

    private int size;

    private Map<Property, TypedValue<?>> properties;

    public <T> void putProperty(Property name, T value, Class<T> type) {
        properties.put(name, new TypedValue<>(type, value));
    }

    public <T> T getProperty(Property name, Class<T> type) {
        TypedValue<?> tv = properties.get(name);
        return type.cast(tv.value);
    }

    public Class<?> getPropertyType(Property name) {
        TypedValue<?> tv = properties.get(name);
        return tv.type;
    }

    public IslandMap() {
        properties = new HashMap<>();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
//
}
