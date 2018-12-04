package pfe.terrain.gen.algo.types;

import com.google.gson.JsonElement;

public class OptionalBooleanType extends BooleanType {

    public OptionalBooleanType(boolean value) {
        super(value);
    }

    @Override
    public JsonElement serialize() {
        return value ? super.serialize() : null;
    }

}
