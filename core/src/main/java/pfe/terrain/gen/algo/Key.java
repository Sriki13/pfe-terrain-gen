package pfe.terrain.gen.algo;

import java.util.Objects;

public class Key<T> {

    private final String identifier;
    private final Class<T> type;

    public Key(String identifier, Class<T> type) {
        this.identifier = identifier;
        this.type = type;
    }

    public String getId() {
        return identifier;
    }

    public Class<T> getType() {
        return type;
    }

    public boolean isSerialized() {
        return false;
    }

    public String getSerializedName() {
        throw new UnsupportedOperationException("The key " + identifier + " is not serializable!");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key<?> key = (Key<?>) o;
        return Objects.equals(identifier, key.identifier) &&
                Objects.equals(type, key.type);
    }

    @Override
    public String toString() {
        return "Key{" +
                "identifier :" + identifier +
                ", type : " + type.getName() +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, type);
    }
}
