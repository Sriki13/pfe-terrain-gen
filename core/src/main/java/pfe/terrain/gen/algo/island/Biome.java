package pfe.terrain.gen.algo.island;

import pfe.terrain.gen.algo.types.SerializableType;

public enum Biome implements SerializableType {

    MANGROVE("MAN", 0.8),
    SNOW("SNO", 0.1),
    TROPICAL_RAIN_FOREST("trF", 0.8),
    TROPICAL_SEASONAL_FOREST("trS", 0.9),
    TAIGA("TAI", 0.75),
    TEMPERATE_RAIN_FOREST("teR", 0.9),
    TEMPERATE_DECIDUOUS_FOREST("teF", 1.0),
    GRASSLAND("GRA", 0.2),
    SHRUBLAND("SHR", 0.2),
    TUNDRA("TUN", 0.2),
    ALPINE("ALP", 0.1),
    BEACH("BEA", 0.1),
    SUB_TROPICAL_DESERT("STD", 0),
    TEMPERATE_DESERT("teD", 0.05),
    OCEAN("OCE", 0.0),
    LAKE("LAK", 0.0),
    GLACIER("GLA", 0.0);

    private String code;
    private double treeDensity;

    Biome(String code, double treeDensity) {
        this.code = code;
        this.treeDensity = treeDensity;
    }

    public static Biome findByCode(String code) {
        for (Biome v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        return null;
    }

    public double getTreeDensity() {
        return treeDensity;
    }

    public String getCode() {
        return code;
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
