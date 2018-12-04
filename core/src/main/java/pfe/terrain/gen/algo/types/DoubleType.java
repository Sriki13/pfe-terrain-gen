package pfe.terrain.gen.algo.types;

public class DoubleType implements SerializableType<Double> {

    public double value;

    public DoubleType(double value) {
        this.value = value;
    }

    @Override
    public Double serialize() {
        return value;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
