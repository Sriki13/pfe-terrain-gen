package pfe.terrain.gen.algo.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BooleanType implements SerializableType {

    public boolean value;

    public BooleanType(boolean value) {
        this.value = value;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(value);
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
