package pfe.terrain.gen.algo.key;

import pfe.terrain.gen.algo.types.SerializableType;

public class SerializableKey<T extends SerializableType> extends Key<T> {

    private String serializedName;

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

}
