package pfe.terrain.gen.algo.constraints.key;

import pfe.terrain.gen.algo.types.SerializableType;

public class SerializableKey<T extends SerializableType> extends Key<T> {

    private Key wrapped;
    private String serializedName;

    public SerializableKey(Key<T> wrapped, String serializedName) {
        super(wrapped.getId(), wrapped.getType());
        this.wrapped = wrapped;
        this.serializedName = serializedName;
    }

    public SerializableKey(String identifier, String serializedName, Class<T> type) {
        super(identifier, type);
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    @Override
    public boolean isSerialized() {
        return true;
    }

    @Override
    public boolean isOptional() {
        if (wrapped == null) {
            return false;
        }
        return wrapped.isOptional();
    }

}
