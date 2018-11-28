package pfe.terrain.gen.algo.types;

public class DoubleType implements SerializableType {

    public double value;

    public DoubleType(double value) {
        this.value = value;
    }

    @Override
    public String serialize() {
        return Double.toString(value);
    }

    @Override
    public String toString() {
        return serialize();
    }
}
