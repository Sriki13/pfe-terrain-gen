package pfe.terrain.gen.algo.biome;

import pfe.terrain.gen.algo.geometry.Face;

import java.util.HashMap;
import java.util.Map;

public class BiomeMap extends HashMap<Face, Biome> {

    public BiomeMap(Map<? extends Face, ? extends Biome> m) {
        super(m);
    }

}
