package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NoiseMap {

    private Map<Coord, Double> heightMap;
    private OpenSimplexNoise noise;

    public NoiseMap(Set<Coord> vertices, long seed) {
        this.heightMap = new HashMap<>();
        this.noise = new OpenSimplexNoise(seed);
        for (Coord vertex : vertices) {
            heightMap.put(vertex, 0.0);
        }
    }

    public void addSimplexNoise(double intensity, double frequency) {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            Coord vertex = entry.getKey();
            heightMap.put(vertex, entry.getValue() + intensity * noise.eval(frequency * vertex.x, frequency * vertex.y));
        }
    }

    // Low factor = everything pulled towards the summit
    // High factor = everything pulled to the bottom
    public void redistribute(double factor) {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            heightMap.put(entry.getKey(), entry.getValue() + Math.pow(entry.getValue(), factor));
        }
    }

    public void putValuesInRange(double seaLevel) {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            heightMap.put(entry.getKey(), ((entry.getValue() + 1) * 20) - seaLevel);
        }
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            if (entry.getValue() < -20) {
                heightMap.put(entry.getKey(), -20.0);
            } else if (entry.getValue() > 20) {
                heightMap.put(entry.getKey(), 20.0);
            }
        }
    }

    public void ensureBordersAreLow() throws NoSuchKeyException, KeyTypeMismatch {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            Coord vertex = entry.getKey();
            if (vertex.getProperty(OpenSimplexHeight.verticeBorderKey).value
                    && heightMap.get(vertex) > 0) {
                heightMap.put(vertex, 0.0);
            }
        }
    }

    public void putHeightProperty() throws DuplicateKeyException {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            entry.getKey().putProperty(OpenSimplexHeight.vertexHeightKey, new DoubleType(entry.getValue()));
        }
    }

}
