package pfe.terrain.gen.algo;

import pfe.terrain.gen.algo.types.SerializableType;

public enum WaterKind implements SerializableType {

    OCEAN("OCEAN"),
    LAKE("LAKE");

    private String name;

    WaterKind(String name) {
        this.name = name;
    }

    @Override
    public String serialize() {
        return name;
    }

}
