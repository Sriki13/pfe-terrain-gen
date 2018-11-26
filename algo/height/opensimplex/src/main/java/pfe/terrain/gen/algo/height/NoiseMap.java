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

    private Map<Coord, Coord> newToOriginal;
    private Map<Coord, Double> heightMap;
    private OpenSimplexNoise noise;

    public NoiseMap(Set<Coord> vertices, long seed, int islandSize)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        this.heightMap = new HashMap<>();
        this.newToOriginal = new HashMap<>();
        this.noise = new OpenSimplexNoise(seed);
        for (Coord vertex : vertices) {
            Coord normalized = normalize(vertex, islandSize);
            normalized.putProperty(OpenSimplexHeight.vertexBorderKey,
                    vertex.getProperty(OpenSimplexHeight.vertexBorderKey));
            newToOriginal.put(normalized, vertex);
            heightMap.put(normalized, 0.0);
        }
    }

    private Coord normalize(Coord vertex, int islandSize) {
        return new Coord(
                vertex.x * 1600 / islandSize,
                vertex.y * 1600 / islandSize
        );
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

    public void putValuesInRange(double seaLevel, double islandSize) {
        double maxWidth = islandSize * 0.5 - 10.0;
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            if (entry.getValue() > 0.5) {
                Coord vertex = entry.getKey();
                double xDist = Math.abs(vertex.x - islandSize * 0.5);
                double yDist = Math.abs(vertex.y - islandSize * 0.5);
                double distance = Math.sqrt(xDist * xDist + yDist * yDist);

                double delta = distance / maxWidth;
                double gradient = delta * delta;

                heightMap.put(entry.getKey(), entry.getValue() * Math.max(0.0, 1.0 - gradient));
            }
            heightMap.put(entry.getKey(), ((entry.getValue() + 1) * 20) - seaLevel);
        }
    }

    public void ensureBordersAreLow() throws NoSuchKeyException, KeyTypeMismatch {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            if (entry.getKey().getProperty(OpenSimplexHeight.vertexBorderKey).value && entry.getValue() > 0) {
                heightMap.put(entry.getKey(), 0.0);
            }
        }
    }

    public void putHeightProperty() throws DuplicateKeyException {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            newToOriginal.get(entry.getKey())
                    .putProperty(OpenSimplexHeight.vertexHeightKey, new DoubleType(entry.getValue()));
        }
    }

}
