package pfe.terrain.gen.algo.island;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import pfe.terrain.gen.algo.types.SerializableType;

public enum Biome implements SerializableType {

    MANGROVE("MAN", 0.6),
    SNOW("SNO", 0.03),
    TROPICAL_RAIN_FOREST("trF", 0.75),
    TROPICAL_SEASONAL_FOREST("trS", 0.85),
    TAIGA("TAI", 0.65),
    TEMPERATE_RAIN_FOREST("teR", 0.9),
    TEMPERATE_DECIDUOUS_FOREST("teF", 1.0),
    GRASSLAND("GRA", 0.15),
    SHRUBLAND("SHR", 0.2),
    TUNDRA("TUN", 0.1),
    ALPINE("ALP", 0.05),
    BEACH("BEA", 0.03),
    SUB_TROPICAL_DESERT("STD", 0),
    TEMPERATE_DESERT("teD", 0.03),
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

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(code);
    }

    @Override
    public String toString() {
        return name();
    }

}
