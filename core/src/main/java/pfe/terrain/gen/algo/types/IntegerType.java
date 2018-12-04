package pfe.terrain.gen.algo.types;

public class IntegerType implements SerializableType<Integer> {

    public int value;

    public IntegerType(int value) {
        this.value = value;
    }

    @Override
    public Integer serialize() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
