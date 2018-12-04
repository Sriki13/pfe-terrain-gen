package pfe.terrain.gen.water;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.*;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.algo.island.WaterKind.*;

public class WaterFromHeightTest {

    private WaterFromHeight generator;
    private IslandMap islandMap;
    private CoordSet allCoords;
    private FaceSet allFaces;

    @Before
    public void setUp() {
        generator = new WaterFromHeight();
        islandMap = new IslandMap();
        allCoords = new CoordSet();
        allFaces = new FaceSet();
        islandMap.putProperty(Contract.FACES, allFaces);
        islandMap.putProperty(Contract.VERTICES, allCoords);
    }

    private Face generateFace(boolean border, boolean hasLandVertices, int seed) {
        int z = hasLandVertices ? 5 : -5;
        Face face = new Face(new Coord(seed, 5), new HashSet<>(Collections.singleton(
                new Edge(generateCoord(0, seed, z), generateCoord(seed, 0, -5))
        )));
        if (border) {
            face.putProperty(WaterFromHeight.FACE_BORDER_KEY, new MarkerType());
        }
        allFaces.add(face);
        return face;
    }

    private Coord generateCoord(int x, int y, int z) {
        Coord coord = new Coord(x, y);
        coord.putProperty(WaterFromHeight.HEIGHT_KEY, new DoubleType(z));
        allCoords.add(coord);
        return coord;
    }


    @Test
    public void oceanPropagationTest() {
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
        assertThat(lake.getProperty(WaterFromHeight.FACE_WATER_KEY).value, is(true));
        assertThat(lake.getProperty(WaterFromHeight.WATER_KIND_KEY), is(LAKE));
    }

    private void assertOcean(Face ocean) {
        assertThat(ocean.getProperty(WaterFromHeight.FACE_WATER_KEY).value, is(true));
        assertThat(ocean.getProperty(WaterFromHeight.WATER_KIND_KEY), is(OCEAN));
    }

    @Test
    public void mappingLandTest() {
        Face land = generateFace(false, true, 1);
        generator.execute(islandMap, new Context());
        assertThat(land.getProperty(WaterFromHeight.FACE_WATER_KEY).value, is(false));
        assertThat(land.getProperty(WaterFromHeight.WATER_KIND_KEY), is(NONE));
    }

}
