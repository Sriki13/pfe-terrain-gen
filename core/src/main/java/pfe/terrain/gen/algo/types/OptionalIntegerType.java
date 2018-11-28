package pfe.terrain.gen.algo.types;

public class OptionalIntegerType extends IntegerType {

    public OptionalIntegerType(int value) {
        super(value);
    }

    @Override
    public String serialize() {
        return value > 0 ? super.serialize() : null;
    }

}
