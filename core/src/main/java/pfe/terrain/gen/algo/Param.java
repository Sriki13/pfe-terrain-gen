package pfe.terrain.gen.algo;

public class Param<T> extends Key<T> {

    private String description;
    private T defaultValue;

    public Param(String identifier, Class<T> type, String range, String descr, T defaultValue) {
        super(identifier, type);
        this.description = type.getSimpleName() + " in " + range + " : " + descr + " -- Default value : " + defaultValue.toString();
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public T getDefaultValue() {
        return defaultValue;
    }
}
