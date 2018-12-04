package pfe.terrain.gen.algo.types;

public class BooleanType implements SerializableType<Boolean> {

    public boolean value;

    public BooleanType(boolean value) {
        this.value = value;
    }

    @Override
    public Boolean serialize() {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
