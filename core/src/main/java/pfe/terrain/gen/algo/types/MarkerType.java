package pfe.terrain.gen.algo.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class MarkerType implements SerializableType {

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(true);
    }

}
