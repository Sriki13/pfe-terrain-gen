package pfe.terrain.gen.algo.types;

import pfe.terrain.gen.algo.geometry.Coord;

import java.util.Set;

public class TreeType implements SerializableType {

    private Set<Coord> value;

    public TreeType(Set<Coord> value) {
        this.value = value;
    }

    @Override
    public String serialize() {
        return value.toString();
    }

    @Override
    public String toString() {
        return serialize();
    }
}
