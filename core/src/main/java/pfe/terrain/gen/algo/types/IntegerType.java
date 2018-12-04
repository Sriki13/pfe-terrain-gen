package pfe.terrain.gen.algo.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class IntegerType implements SerializableType {

    public int value;

    public IntegerType(int value) {
        this.value = value;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
