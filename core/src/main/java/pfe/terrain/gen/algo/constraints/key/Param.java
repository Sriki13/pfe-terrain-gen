package pfe.terrain.gen.algo.constraints.key;

import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;

public class Param<T> extends Key<T> {

    private String description;
    private String label;
    private T defaultValue;

    private Comparable min;
    private Comparable max;

    public Param(String identifier, Class<T> type, String range, String description,
                 T defaultValue, String label) {
        super(identifier, type);
        init(type, range, description, defaultValue, label);
    }

    public Param(String id, Class<T> type, Comparable min, Comparable max, String description,
                 T defaultValue, String label) {
        super(id, type);
        init(type, min + "-" + max, description, defaultValue, label);
        this.min = min;
        this.max = max;
    }

    public static Param<Double> generateDefaultDoubleParam(String id, String description,
                                                           double defaultValue, String label) {
        return new Param<>(id, Double.class, 0.0, 1.0, description, defaultValue, label);
    }

    public static Param<Integer> generatePositiveIntegerParam(String id, Comparable max, String description,
                                                              int defaultValue, String label) {
        return new Param<>(id, Integer.class, 0, max, description, defaultValue, label);
    }

    private void init(Class<T> type, String range, String description,
                      T defaultValue, String label) {
        this.description = type.getSimpleName() + " in " + range + " : " + description
                + " -- Default value : " + defaultValue.toString();
        this.defaultValue = defaultValue;
        this.label = label;
    }

    public void checkValue(T value) {
        if (min != null) {
            Comparable val = ((Comparable) value);
            //noinspection unchecked
            if (val.compareTo(min) < 0 || val.compareTo(max) > 0) {
                throw new InvalidAlgorithmParameters(getId(), val.toString(), min.toString(), max.toString());
            }
        }
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

    public T getDefaultValue() {
        return defaultValue;
    }
}
