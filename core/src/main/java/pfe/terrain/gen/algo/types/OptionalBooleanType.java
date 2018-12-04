package pfe.terrain.gen.algo.types;

public class OptionalBooleanType extends BooleanType {

    public OptionalBooleanType(boolean value) {
        super(value);
    }

    @Override
    public Boolean serialize() {
        return value ? true : null;
    }

}
