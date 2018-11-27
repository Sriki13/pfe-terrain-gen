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

    private static final int BASE_SIZE = 1600;

    private Map<Coord, Coord> newToOriginal;
    private Map<Coord, Double> heightMap;

    public NoiseMap(Set<Coord> vertices, int islandSize)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        this.heightMap = new HashMap<>();
        this.newToOriginal = new HashMap<>();
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
                vertex.x * BASE_SIZE / islandSize,
                vertex.y * BASE_SIZE / islandSize
        );
    }

    public void addSimplexNoise(long seed, double intensity, double frequency) {
        OpenSimplexNoise noise = new OpenSimplexNoise(seed);
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            Coord vertex = entry.getKey();
            double value = intensity * noise.eval(frequency * vertex.x, frequency * vertex.y);
            heightMap.put(vertex, entry.getValue() + ((value + 1) / 2));
        }
    }

    public void putValuesInRange() {
        double maxWidth = BASE_SIZE * 0.5 - 10.0;
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            if (entry.getValue() > 0.5) {
                Coord vertex = entry.getKey();
                double xDist = Math.abs(vertex.x - BASE_SIZE * 0.5);
                double yDist = Math.abs(vertex.y - BASE_SIZE * 0.5);
                double distance = Math.sqrt(xDist * xDist + yDist * yDist);

                double delta = distance / maxWidth;
                double gradient = delta * delta;

                heightMap.put(entry.getKey(), entry.getValue() * Math.max(0.0, 1.0 - gradient));
            }
            double normalized = 40 * entry.getValue() - 20;
            heightMap.put(entry.getKey(), normalized);
        }
    }

    public void lower(double amount) {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            heightMap.put(entry.getKey(), entry.getValue() - amount);
        }
    }

    public void ensureBordersAreLow() throws NoSuchKeyException, KeyTypeMismatch {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            if (entry.getKey().getProperty(OpenSimplexHeight.vertexBorderKey).value && entry.getValue() > 0) {
                heightMap.put(entry.getKey(), 0.0);
            }
        }
    }

    public void multiplyHeights(double factor) {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            heightMap.put(entry.getKey(), entry.getValue() * factor);
        }
    }

    public void redistribute(double factor) {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            heightMap.put(entry.getKey(), Math.pow(entry.getValue(), factor));
        }
    }

    public void putHeightProperty() throws DuplicateKeyException {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            newToOriginal.get(entry.getKey())
                    .putProperty(OpenSimplexHeight.vertexHeightKey, new DoubleType(entry.getValue()));
        }
    }

}
