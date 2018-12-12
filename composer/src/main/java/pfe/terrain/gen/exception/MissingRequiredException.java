package pfe.terrain.gen.exception;

import pfe.terrain.gen.algo.constraints.key.Key;

import java.util.Collection;

public class MissingRequiredException extends Exception {

    public MissingRequiredException(String element) {
        super("A required elements is missing: " + element);
    }

    public MissingRequiredException(Collection<Key> keys){
        super(buildString(keys));
    }

    private static String buildString(Collection<Key> keys){
        StringBuilder builder = new StringBuilder("missing element : \n");

        for(Key key : keys){
            builder.append(key.getId());
        }
        return builder.toString();
    }
}
