package pfe.terrain.gen;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.algorithms.WaterGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Random;

public class RadialWaterGeneration extends WaterGenerator {

    @Override
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, KeyTypeMismatch, NoSuchKeyException {
        Shape shape = new Shape(map.getSize(), 1.0, new Random(map.getSeed()));
        for (Face face : map.getFaces()) {
            face.putProperty(faceWaterKey, new BooleanType(shape.check(face.getCenter().x, face.getCenter().y)));
        }
    }

    class Shape {
        private int bumps;
        private double startAngle;
        private double dipAngle;
        private double dipWidth;
        private int size;
        private Double factor;

        public Shape(int islandSize, Double factor, Random random) {
            this.factor = factor;
            size = islandSize;
            bumps = random.nextInt(5) + 1;
            startAngle = random.nextDouble() * 2 * Math.PI;
            dipAngle = random.nextDouble() * 2 * Math.PI;
            dipWidth = 0.2 + random.nextDouble() * 0.5;
        }

        public boolean check(double x, double y) {
            double angle = Math.atan2(x, y);
            double length = 0.5 * (Math.max(Math.abs(x), Math.abs(y)) + Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
            boolean threshold = Math.abs(angle - dipAngle) < dipWidth ||
                    Math.abs(angle - dipAngle + 2 * Math.PI) < dipWidth ||
                    Math.abs(angle - dipAngle - 2 * Math.PI) < dipWidth;

            double r1;
            if (threshold) {
                r1 = 0.2;
            } else {
                r1 = 0.5 + 0.4 * Math.sin(startAngle + bumps * angle + Math.cos((bumps + 3) * angle));
            }
            double r2;
            if (threshold) {
                r2 = 0.2;
            } else {
                r2 = 0.7 - 0.2 * Math.sin(startAngle + bumps * angle - Math.sin((bumps + 2) * angle));
            }

            return !(length < r1 || (length > r1 * factor && length < r2));
        }
    }
}
