package pfe.terrain.gen.algo.types;

public class BooleanType implements SerializableType {

    public boolean value;

    public BooleanType(boolean value) {
        this.value = value;
    }

    @Override
    public String toJSON() {
        return Boolean.toString(value);
    }

}
