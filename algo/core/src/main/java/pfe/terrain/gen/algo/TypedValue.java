package pfe.terrain.gen.algo;

class TypedValue<T> {
    public final Class<T> type;
    public final T value;

    TypedValue(Class<T> type, T value) {
        this.type = type;
        this.value = value;
    }
}
