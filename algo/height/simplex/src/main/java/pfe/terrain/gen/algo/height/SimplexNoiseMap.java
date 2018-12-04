package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.*;

public class SimplexNoiseMap {

    private static final int BASE_SIZE = 1600;

    private Map<Coord, Coord> newToOriginal;
    private Map<Coord, Double> heightMap;
    private SimplexNoise noise;

    public SimplexNoiseMap(Set<Coord> vertices, int islandSize, int seed)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        this.heightMap = new HashMap<>();
        this.newToOriginal = new HashMap<>();
        this.noise = new SimplexNoise(seed);
        for (Coord vertex : vertices) {
            Coord normalized = normalize(vertex, islandSize);
            if (vertex.hasProperty(SimplexHeight.VERTEX_BORDER_KEY)) {
                normalized.putProperty(SimplexHeight.VERTEX_BORDER_KEY, new MarkerType());
            }
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

    public void addSimplexNoise(double intensity, double frequency, double islandCoefficient, SimplexIslandShape shape) {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            Coord vertex = entry.getKey();
            double nx = (vertex.x / BASE_SIZE - 0.5);
            double ny = (vertex.y / BASE_SIZE - 0.5);
            double value = intensity * noise.eval(frequency * nx, frequency * ny);
            double distance = shape.distance(nx, ny);
            value = (value + islandCoefficient) * (1 - 1.4 * Math.pow(distance, 1.2));
            heightMap.put(vertex, entry.getValue() + (value / 2 + 0.5));
        }
    }

    public void setWaterLevel(double level) throws NoSuchKeyException, KeyTypeMismatch {
        List<Double> heightList = new ArrayList<>();
        for (Map.Entry<Coord, Coord> entry : newToOriginal.entrySet()) {
            if (!entry.getValue().hasProperty(SimplexHeight.VERTEX_BORDER_KEY)) {
                heightList.add(heightMap.get(entry.getKey()));
            }
        }
        Collections.sort(heightList);
        double waterLevel = heightList.get((int) (level * heightList.size()));
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            heightMap.put(entry.getKey(), entry.getValue() - waterLevel);
        }
    }

    public void ensureBordersAreLow() throws NoSuchKeyException, KeyTypeMismatch {
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            if (entry.getKey().hasProperty(SimplexHeight.VERTEX_BORDER_KEY) && entry.getValue() > 0) {
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
                    .putProperty(SimplexHeight.VERTEX_HEIGHT_KEY, new DoubleType(entry.getValue()));
        }
    }

}
