package pfe.terrain.gen.algo.constraints.key;

public class PermanentKey<T> extends Key<T> {

    public PermanentKey(String identifier, Class<T> type) {
        super(identifier, type);
    }

    @Override
    public boolean isPermanent() {
        return true;
    }
}
