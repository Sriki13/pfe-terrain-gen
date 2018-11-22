package pfe.terrain.gen.algo.types;

public class IntegerType implements SerializableType {

    public int value;

    public IntegerType(int value) {
        this.value = value;
    }

    @Override
    public String serialize() {
        return Integer.toString(value);
    }

}
