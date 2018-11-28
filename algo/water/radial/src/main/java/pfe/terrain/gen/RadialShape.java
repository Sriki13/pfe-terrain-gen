package pfe.terrain.gen;

import java.util.Random;

public class RadialShape {
    private int bumps;
    private double startAngle;
    private double dipAngle;
    private double dipWidth;
    private double factor;
    private double islandSize;

    public RadialShape(double islandSize, double factor, Random random) {
        this.factor = factor;
        this.islandSize = (1 - islandSize);
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