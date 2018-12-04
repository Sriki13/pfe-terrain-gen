package pfe.terrain.gen.algo.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import pfe.terrain.gen.algo.geometry.Coord;

import java.util.Set;

public class TreeType implements SerializableType {

    private Set<Coord> value;

    public TreeType(Set<Coord> value) {
        this.value = value;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(value.toString());
    }

    @Override
    public String toString() {
        return "";
    }
}
