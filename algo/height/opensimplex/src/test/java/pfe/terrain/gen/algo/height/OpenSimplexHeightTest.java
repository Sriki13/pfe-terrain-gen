package pfe.terrain.gen.algo.height;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.*;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class OpenSimplexHeightTest {

    private IslandMap islandMap;
    private OpenSimplexHeight height;

    private Face borderFace;
    private Face faceWithABorderVertex;
    private Face normalFace;

    @Before
    public void setUp() throws Exception {
        islandMap = new IslandMap();
        height = new OpenSimplexHeight();
        CoordSet vertices = new CoordSet(new HashSet<>());
        borderFace = new Face(generateCoord(vertices, false, -1), Collections.singleton(
                new Edge(generateCoord(vertices, true, 1), generateCoord(vertices, true, 2))
        ));
        borderFace.putProperty(OpenSimplexHeight.faceBorderKey, new BooleanType(true));
        faceWithABorderVertex = new Face(generateCoord(vertices, false, 3), new HashSet<>(Arrays.asList(
                new Edge(generateCoord(vertices, false, 4), generateCoord(vertices, false, 5)),
                new Edge(generateCoord(vertices, false, 6), generateCoord(vertices, true, 7))
        )));
        faceWithABorderVertex.putProperty(OpenSimplexHeight.faceBorderKey, new BooleanType(true));
        normalFace = new Face(generateCoord(vertices, false, 8), Collections.singleton(
                new Edge(generateCoord(vertices, false, 9), generateCoord(vertices, false, 10))
        ));
        normalFace.putProperty(OpenSimplexHeight.faceBorderKey, new BooleanType(false));
        FaceSet faces = new FaceSet(new HashSet<>(Arrays.asList(
                borderFace, faceWithABorderVertex, normalFace
        )));
        islandMap.putProperty(Contract.faces, faces);
        islandMap.putProperty(Contract.vertices, vertices);
        islandMap.putProperty(Contract.size, 1600);
    }

    private Coord generateCoord(Collection<Coord> allCoords, boolean isBorder, int seed) throws Exception {
        Coord coord = new Coord(300, 300 + seed);
        coord.putProperty(OpenSimplexHeight.vertexBorderKey, new BooleanType(isBorder));
        allCoords.add(coord);
        return coord;
    }

    @Test
    public void generateHeightTest() throws Exception {
        Context context = new Context();
        // we guarantee that everything will be very tall
        OpenSimplexHeight.setIntensity(100);
        islandMap.putProperty(Contract.seed, 25);
        height.execute(islandMap, context);
        for (Coord vertex : islandMap.getVertices()) {
            assertThat(vertex.getProperty(OpenSimplexHeight.vertexHeightKey), notNullValue());
        }
        assertLow(borderFace);
        assertLow(faceWithABorderVertex);
        for (Coord coord : normalFace.getBorderVertices()) {
            assertThat(coord.getProperty(OpenSimplexHeight.vertexHeightKey).value, greaterThan(0.0));
        }
        assertThat(normalFace.getCenter().getProperty(OpenSimplexHeight.vertexHeightKey).value, greaterThan(0.0));
    }

    private void assertLow(Face face) throws Exception {
        for (Coord coord : face.getBorderVertices()) {
            assertLow(coord);
        }
        assertLow(face.getCenter());
    }

    private void assertLow(Coord coord) throws Exception {
        assertThat(coord.getProperty(OpenSimplexHeight.vertexHeightKey).value, closeTo(0.0, 0.001));
    }

}
