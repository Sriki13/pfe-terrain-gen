package pfe.terrain.gen.algo.borders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import pfe.terrain.gen.algo.types.SerializableType;

import java.util.List;

public class TreeType implements SerializableType {

    private List<JsonCoord> value;

    public TreeType(List<JsonCoord> value) {
        this.value = value;
    }

    @Override
    public JsonElement serialize() {
        Gson gson = new Gson();
        return gson.toJsonTree(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
