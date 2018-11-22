package pfe.terrain.gen.algo.types;

public class OptionalBooleanType extends BooleanType {

    public OptionalBooleanType(boolean value) {
        super(value);
    }

    @Override
    public String serialize() {
        if (!value) {
            return null;
        } else return "true";
    }

}
