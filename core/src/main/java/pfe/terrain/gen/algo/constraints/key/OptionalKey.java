package pfe.terrain.gen.algo.constraints.key;

public class OptionalKey<T> extends Key<T> {

    public OptionalKey(String identifier, Class<T> type) {
        super(identifier, type);
    }

    @Override
    public boolean isOptional() {
        return true;
    }

}
