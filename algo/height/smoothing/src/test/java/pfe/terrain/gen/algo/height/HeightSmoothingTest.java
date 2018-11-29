package pfe.terrain.gen.algo.height;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.*;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static pfe.terrain.gen.algo.height.HeightSmoothing.vertexHeightKey;
import static pfe.terrain.gen.algo.height.HeightSmoothing.vertexWaterKey;

public class HeightSmoothingTest {

    private IslandMap map;
    private CoordSet coords;
    private int mapSize;

    @Before
    public void setUp() throws Exception {
        map = new IslandMap();
        map.putProperty(new Key<>("SIZE", Integer.class), mapSize);
        map.putProperty(new Key<>("SEED", Integer.class), 3);
        Random random = new Random(map.getSeed());
        coords = new CoordSet();
        EdgeSet edges = new EdgeSet();
        mapSize = 128;
        List<Coord> coordsMatrix = new ArrayList<>(Collections.nCopies(mapSize * mapSize, new Coord(0, 0)));
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                Coord coord = new Coord(i, j);
                if (i <= 0 || i >= mapSize - 1 || j <= 0 || j >= mapSize - 1) {
                    coord.putProperty(vertexWaterKey, new BooleanType(true));
                    coord.putProperty(vertexHeightKey, new DoubleType(0.0));
                } else {
                    coord.putProperty(vertexWaterKey, new BooleanType(false));
                    coord.putProperty(vertexHeightKey, new DoubleType(random.nextDouble()));
                }
                coords.add(coord);
                coordsMatrix.set(j * mapSize + i, coord);
            }
        }
        for (int i = 1; i < mapSize - 1; i += 1) {
            for (int j = 1; j < mapSize - 1; j += 1) {
                edges.add(new Edge(coordsMatrix.get(j * mapSize + i), coordsMatrix.get(j * mapSize + i + 1)));
                edges.add(new Edge(coordsMatrix.get(j * mapSize + i), coordsMatrix.get((j + 1) * mapSize + i)));
            }
        }
        FaceSet faces = new FaceSet();
        map.putProperty(new Key<>("VERTICES", CoordSet.class), coords);
        map.putProperty(new Key<>("EDGES", EdgeSet.class), edges);
        map.putProperty(new Key<>("FACES", FaceSet.class), faces);
    }

    @Test
    public void testSmoothening() throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        double stdDevBefore = getStandardDeviationFromCoordSet();
        new HeightSmoothing().execute(map, new Context());
        double stdDevAfter = getStandardDeviationFromCoordSet();
        assertThat(stdDevBefore, greaterThan(stdDevAfter));
    }

    private double getStandardDeviationFromCoordSet() {
        return map.getVertices().stream().map(c -> {
            try {
                return c.getProperty(vertexHeightKey).value;
            } catch (NoSuchKeyException | KeyTypeMismatch e) {
                e.printStackTrace();
            }
            return 0.0;
        }).collect(DoubleStatistics.collector()).getStandardDeviation();
    }

    @Ignore
    @Test
    public void testVisualizeFinal() throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        toImage("before");
        Context context = new Context();
        context.putParam(HeightSmoothing.smoothingFactor, 1.0);
        new HeightSmoothing().execute(map, context);
        toImage("after");

    }

    private void toImage(String name) throws NoSuchKeyException, KeyTypeMismatch {
        coords = map.getVertices();
        final BufferedImage image = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_USHORT_GRAY);
        short[] data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
        for (Coord coord : coords) {
            double height = coord.getProperty(vertexHeightKey).value;
            data[Math.toIntExact(Math.round(coord.y * mapSize + coord.x))] = (short) (height * 65000);
        }
        try {
            ImageIO.write(image, "PNG", new File(name + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class DoubleStatistics extends DoubleSummaryStatistics {

        private double sumOfSquare = 0.0d;
        private double sumOfSquareCompensation;
        private double simpleSumOfSquare;

        @Override
        public void accept(double value) {
            super.accept(value);
            double squareValue = value * value;
            simpleSumOfSquare += squareValue;
            sumOfSquareWithCompensation(squareValue);
        }

        DoubleStatistics combine(DoubleStatistics other) {
            super.combine(other);
            simpleSumOfSquare += other.simpleSumOfSquare;
            sumOfSquareWithCompensation(other.sumOfSquare);
            sumOfSquareWithCompensation(other.sumOfSquareCompensation);
            return this;
        }

        private void sumOfSquareWithCompensation(double value) {
            double tmp = value - sumOfSquareCompensation;
            double velvel = sumOfSquare + tmp; // Little wolf of rounding error
            sumOfSquareCompensation = (velvel - sumOfSquare) - tmp;
            sumOfSquare = velvel;
        }

        double getSumOfSquare() {
            double tmp = sumOfSquare + sumOfSquareCompensation;
            if (Double.isNaN(tmp) && Double.isInfinite(simpleSumOfSquare)) {
                return simpleSumOfSquare;
            }
            return tmp;
        }

        final double getStandardDeviation() {
            long count = getCount();
            double sumOfSquare = getSumOfSquare();
            double average = getAverage();
            return count > 0 ? Math.sqrt((sumOfSquare - count * Math.pow(average, 2)) / (count - 1)) : 0.0d;
        }

        static Collector<Double, ?, DoubleStatistics> collector() {
            return Collector.of(DoubleStatistics::new, DoubleStatistics::accept, DoubleStatistics::combine);
        }

    }
}

