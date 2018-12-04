package pfe.terrain.gen.algo.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class DoubleType implements SerializableType {

    public double value;

    public DoubleType(double value) {
        this.value = value;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(value);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
