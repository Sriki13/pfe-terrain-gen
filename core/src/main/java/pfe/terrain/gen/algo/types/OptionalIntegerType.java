package pfe.terrain.gen.algo.types;

import com.google.gson.JsonElement;

public class OptionalIntegerType extends IntegerType {

    public OptionalIntegerType(int value) {
        super(value);
    }

    @Override
    public JsonElement serialize() {
        return value > 0 ? super.serialize() : null;
    }

}
