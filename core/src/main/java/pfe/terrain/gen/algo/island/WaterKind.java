package pfe.terrain.gen.algo.island;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import pfe.terrain.gen.algo.types.SerializableType;

public enum WaterKind implements SerializableType {

    OCEAN("OCEAN"),
    LAKE("LAKE"),
    NONE(null);

    private String name;

    WaterKind(String name) {
        this.name = name;
    }

    @Override
    public JsonElement serialize() {
        if (this == NONE) {
            return null;
        }
        return new JsonPrimitive(name);
    }

}
