package pfe.terrain.gen.algo;

public class Param<T> extends Key<T> {

    private String description;
    private String label;
    private T defaultValue;

    public Param(String identifier, Class<T> type, String range, String description,
                 T defaultValue, String label) {
        super(identifier, type);
        this.description = type.getSimpleName() + " in " + range + " : " + description + " -- Default value : " + defaultValue.toString();
        this.defaultValue = defaultValue;
        this.label = label;
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
