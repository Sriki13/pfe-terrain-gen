package pfe.terrain.gen.algo.types;

public class OptionalIntegerType extends IntegerType {

    public OptionalIntegerType(int value) {
        super(value);
    }

    @Override
    public Integer serialize() {
        return value > 0 ? value : null;
    }

}
