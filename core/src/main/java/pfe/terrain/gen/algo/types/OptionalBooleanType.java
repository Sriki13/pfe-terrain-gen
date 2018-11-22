package pfe.terrain.gen.algo.types;

public class OptionalBooleanType extends BooleanType {

    public OptionalBooleanType(boolean value) {
        super(value);
    }

    @Override
    public String toJSON() {
        if (!value) {
            return null;
        } else return "true";
    }

}
