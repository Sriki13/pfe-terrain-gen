package pfe.terrain.gen.algo.types;

public class IntegerType implements SerializableType {

    public int value;

    public IntegerType(int value) {
        this.value = value;
    }

    @Override
    public String toJSON() {
        return Integer.toString(value);
    }

}
