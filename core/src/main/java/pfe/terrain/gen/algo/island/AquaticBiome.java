package pfe.terrain.gen.algo.island;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import pfe.terrain.gen.algo.types.SerializableType;

public enum AquaticBiome implements SerializableType {

    SHALLOW_WATER("sh"),
    OCEAN("oc"),
    DEEP_OCEAN("doc"),
    CORAL_REEF("cor");

    private String code;

    AquaticBiome(String code) {
        this.code = code;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(code);
    }

    @Override
    public String toString() {
        return name();
    }


}
