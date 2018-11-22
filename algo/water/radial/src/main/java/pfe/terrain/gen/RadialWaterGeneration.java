package pfe.terrain.gen;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.WaterKind;
import pfe.terrain.gen.algo.algorithms.WaterGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Random;
import java.util.Set;

public class RadialWaterGeneration extends WaterGenerator {

    private Key<Double> islandSizeK = new Key<>("islandSize", Double.class);
    private Key<Double> islandScatterK = new Key<>("islandScatter", Double.class);

    @Override
    public Set<Key> getRequestedParameters() {
        Set<Key> keys = super.getRequestedParameters();
        keys.add(islandScatterK);
        keys.add(islandSizeK);
        return keys;
    }

    @Override
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, KeyTypeMismatch {
        double islandSize = context.getPropertyOrDefault(islandSizeK, 1.0);
        double factor = context.getPropertyOrDefault(islandScatterK, 0.0);
        Shape shape = new Shape(islandSize, factor + 1, new Random(map.getSeed()));
        int size = map.getSize();
        for (Face face : map.getFaces()) {
            BooleanType isWater = new BooleanType(shape.isWater(2 * (face.getCenter().x / size - 0.5), 2 * (face.getCenter().y / size - 0.5)));
            face.putProperty(faceWaterKey, isWater);
            if (isWater.value) {
                face.putProperty(waterKindKey, WaterKind.OCEAN);
            } else {
                face.putProperty(waterKindKey, WaterKind.NONE);
            }
            face.getCenter().putProperty(vertexWaterKey, isWater);
            for (Coord coord : face.getVertices()) {
                coord.putProperty(vertexWaterKey, isWater);
            }
        }
    }

    class Shape {
        private int bumps;
        private double startAngle;
        private double dipAngle;
        private double dipWidth;
        private double factor;
        private double islandSize;

        public Shape(double islandSize, double factor, Random random) {
            this.factor = factor;
            this.islandSize = (1 - islandSize) * 10;
            bumps = random.nextInt(5) + 1;
            startAngle = random.nextDouble() * 2 * Math.PI;
            dipAngle = random.nextDouble() * 2 * Math.PI;
            dipWidth = random.nextDouble() * 0.9 + 0.2;
        }

        boolean isWater(double x, double y) {
            double angle = Math.atan2(x, y);
            double length = ((0.5 + islandSize) * (Math.max(Math.abs(x), Math.abs(y)) + Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))));
            double r1 = 0.5 + 0.4 * Math.sin(startAngle + bumps * angle + Math.cos((bumps + 3) * angle));
            double r2 = 0.7 - 0.2 * Math.sin(startAngle + bumps * angle - Math.sin((bumps + 3) * angle));
            if (Math.abs(angle - dipAngle) < dipWidth
                    || Math.abs(angle - dipAngle + 2 * Math.PI) < dipWidth
                    || Math.abs(angle - dipAngle - 2 * Math.PI) < dipWidth) {
                r1 = r2 = 0.5;
            }

            return !(length <= r1 || (length >= r1 * factor && length < r2));
        }
    }
}
