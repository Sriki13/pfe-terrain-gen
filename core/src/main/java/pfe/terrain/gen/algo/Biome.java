package pfe.terrain.gen.algo;

import pfe.terrain.gen.algo.types.SerializableType;

public enum Biome implements SerializableType {

    MANGROVE("MAN"),
    SNOW("SNO"),
    TROPICAL_RAIN_FOREST("trF"),
    TROPICAL_SEASONAL_FOREST("trS"),
    TAIGA("TAI"),
    TEMPERATE_RAIN_FOREST("teR"),
    TEMPERATE_DECIDUOUS_FOREST("teF"),
    GRASSLAND("GRA"),
    SHRUBLAND("SHR"),
    TUNDRA("TUN"),
    ALPINE("ALP"),
    BEACH("BEA"),
    SUB_TROPICAL_DESERT("STD"),
    TEMPERATE_DESERT("teD"),
    OCEAN("OCE"),
    LAKE("LAK"),
    GLACIER("GLA");

    private String code;

    Biome(String code) {
        this.code = code;
    }

    public static Biome findByCode(String code) {
        for (Biome v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public String serialize() {
        return code;
    }

    @Override
    public String toString() {
        return name();
    }

}
