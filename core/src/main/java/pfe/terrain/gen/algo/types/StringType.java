package pfe.terrain.gen.algo.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.io.Serializable;

public class StringType implements SerializableType {
    public String value;

    public StringType(String value) {
        this.value = value;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
