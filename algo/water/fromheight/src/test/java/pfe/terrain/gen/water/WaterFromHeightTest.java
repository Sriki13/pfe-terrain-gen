package pfe.terrain.gen.water;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.*;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.algo.WaterKind.*;

public class WaterFromHeightTest {

    private WaterFromHeight generator;
    private IslandMap islandMap;
    private CoordSet allCoords;
    private FaceSet allFaces;

    @Before
    public void setUp() throws Exception {
        generator = new WaterFromHeight();
        islandMap = new IslandMap();
        allCoords = new CoordSet();
        allFaces = new FaceSet();
        islandMap.putProperty(Contract.faces, allFaces);
        islandMap.putProperty(Contract.vertices, allCoords);
    }

    private Face generateFace(boolean border, boolean hasLandVertices, int seed) throws Exception {
        int z = -5;
        if (hasLandVertices) z = 5;
        Face face = new Face(new Coord(seed, 5), new HashSet<>(Collections.singleton(
                new Edge(generateCoord(0, seed, z, border), generateCoord(seed, 0, -5, border))
        )));
        face.putProperty(WaterFromHeightGenerator.faceBorderKey, new BooleanType(border));
        allFaces.add(face);
        return face;
    }

    private Coord generateCoord(int x, int y, int z, boolean border) throws Exception {
        Coord coord = new Coord(x, y);
        coord.putProperty(WaterFromHeightGenerator.heightKey, new DoubleType(z));
        coord.putProperty(WaterFromHeightGenerator.vertexBorderKey, new BooleanType(border));
        allCoords.add(coord);
        return coord;
    }


    @Test
    public void oceanPropagationTest() throws Exception {
        Face ocean = generateFace(true, false, 4);
        Face neighbor = generateFace(false, false, 1);
        ocean.addNeighbor(neighbor);
        Face neighborNeighbor = generateFace(false, false, 2);
        neighbor.addNeighbor(neighborNeighbor);
        Face lake = generateFace(false, false, 3);
        generator.execute(islandMap, new Context());
        assertOcean(ocean);
        assertOcean(neighbor);
        assertOcean(neighborNeighbor);
        assertThat(lake.getProperty(WaterFromHeightGenerator.faceWaterKey).value, is(true));
        assertThat(lake.getProperty(WaterFromHeightGenerator.waterKindKey), is(LAKE));
    }

    private void assertOcean(Face ocean) throws Exception {
        assertThat(ocean.getProperty(WaterFromHeightGenerator.faceWaterKey).value, is(true));
        assertThat(ocean.getProperty(WaterFromHeightGenerator.waterKindKey), is(OCEAN));
    }

    @Test
    public void mappingLandTest() throws Exception {
        Face land = generateFace(false, true, 1);
        Face border = generateFace(true, true, 2);
        generator.execute(islandMap, new Context());
        assertThat(land.getProperty(WaterFromHeightGenerator.faceWaterKey).value, is(false));
        assertThat(land.getProperty(WaterFromHeightGenerator.waterKindKey), is(NONE));
        assertOcean(border);
    }

}
