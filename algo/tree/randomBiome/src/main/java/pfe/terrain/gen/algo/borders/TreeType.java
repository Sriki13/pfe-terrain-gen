package pfe.terrain.gen.algo.borders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import pfe.terrain.gen.algo.types.SerializableType;

import java.util.Arrays;

public class TreeType implements SerializableType {

    private JsonCoord[] value;

    public TreeType(JsonCoord[] value) {
        this.value = value;
    }

    @Override
    public JsonElement serialize() {
        Gson gson = new Gson();
        return gson.toJsonTree(value);
    }

    @Override
    public String toString() {
        return Arrays.toString(value);
    }
}
