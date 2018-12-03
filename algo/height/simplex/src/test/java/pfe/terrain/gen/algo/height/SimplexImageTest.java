package pfe.terrain.gen.algo.height;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.FaceSet;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.types.BooleanType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SimplexImageTest {

    private IslandMap islandMap;
    private SimplexHeight simplexHeight;

    public static final int SIZE = 500;

    @Before
    public void setUp() throws Exception {
        CoordSet allCoords = new CoordSet(new HashSet<>());
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Coord c = new Coord(i, j);
                c.putProperty(SimplexHeight.vertexBorderKey, new BooleanType(isBorder(i, j, SIZE)));
                allCoords.add(c);
            }
        }
        islandMap = new IslandMap();
        islandMap.putProperty(Contract.vertices, allCoords);
        islandMap.putProperty(Contract.size, SIZE);
        islandMap.putProperty(Contract.faces, new FaceSet(new HashSet<>()));
        islandMap.putProperty(Contract.seed, 25);
        simplexHeight = new SimplexHeight();
    }

    public boolean isBorder(int i, int j, int SIZE) {
        return i == 0 || i == SIZE || j == 0 || j == SIZE;
    }


    @Test
    @Ignore
    public void printPerlin() throws Exception {
        simplexHeight.execute(islandMap, new Context());
        Map<Coord, Double> heightMap = new HashMap<>();
        for (Coord coord : islandMap.getVertices()) {
            heightMap.put(coord, coord.getProperty(SimplexHeight.vertexHeightKey).value);
        }
        double max = Collections.max(heightMap.values());
        double min = Collections.min(heightMap.values());
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            heightMap.put(entry.getKey(), (entry.getValue() - min) / (max - min));
        }
        final BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_USHORT_GRAY);
        short[] data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
        for (Map.Entry<Coord, Double> entry : heightMap.entrySet()) {
            data[(int) entry.getKey().y * SIZE + (int) entry.getKey().x] = (short) (entry.getValue() * 65536);
        }
        try {
            ImageIO.write(image, "PNG", new File("noise.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
