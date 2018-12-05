package pfe.terrain.gen.algo.constraints;

import pfe.terrain.gen.algo.constraints.key.Key;

import static pfe.terrain.gen.algo.constraints.Contract.*;

public enum Prefixes {

    VERTICES_P("VERTICES_", VERTICES),
    EDGES_P("EDGES_", EDGES),
    FACES_P("FACES_", FACES);

    private String prefix;
    private Key<?> key;

    Prefixes(String prefix, Key<?> key) {
        this.prefix = prefix;
        this.key = key;
    }

    public String getPrefix() {
        return prefix;
    }

    public Key getKey() {
        return key;
    }
}
