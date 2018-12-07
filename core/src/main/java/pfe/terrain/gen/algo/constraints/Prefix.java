package pfe.terrain.gen.algo.constraints;

import pfe.terrain.gen.algo.constraints.key.Key;

import static pfe.terrain.gen.algo.constraints.Contract.*;

public enum Prefix {

    VERTICES_P(VERTICES_PREFIX, VERTICES),
    EDGES_P(EDGES_PREFIX, EDGES),
    FACES_P(FACES_PREFIX, FACES);

    private String prefix;
    private Key<?> key;

    Prefix(String prefix, Key<?> key) {
        this.prefix = prefix;
        this.key = key;
    }

    public String getPrefix() {
        return prefix;
    }

    public Key getKey() {
        return key;
    }

    @Override
    public String toString() {
        return prefix;
    }

}
