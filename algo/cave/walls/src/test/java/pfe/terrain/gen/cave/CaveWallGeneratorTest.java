package pfe.terrain.gen.cave;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static pfe.terrain.gen.algo.constraints.Contract.FACES;
import static pfe.terrain.gen.cave.CaveWallGenerator.*;

public class CaveWallGeneratorTest {

    private CaveWallGenerator generator;
    private TerrainMap terrainMap;

    private Face emptyFace;
    private Face fullFace;
    private Face almostFullFace;
    private Face almostEmptyFace;

    private int seedCount = 0;

    private int getSeed() {
        seedCount++;
        return seedCount;
    }

    @Before
    public void setUp() {
        generator = new CaveWallGenerator();
        terrainMap = new TerrainMap();
        emptyFace = generateFace(5, 0, false, false);
        fullFace = generateFace(0, 5, true, true);
        almostEmptyFace = generateFace(10, 1, false, false);
        almostFullFace = generateFace(1, 10, true, false);
        FaceSet allFaces = new FaceSet(new HashSet<>(Arrays.asList(
                emptyFace, fullFace, almostEmptyFace, almostFullFace
        )));
        terrainMap.putProperty(FACES, allFaces);
    }

    private Face generateFace(int nbEmpty, int nbFull, boolean centerIsWall, boolean isWall) {
        Set<Edge> edges = new HashSet<>();
        for (int i = 0; i < nbEmpty; i++) {
            edges.add(new Edge(generateCoord(false), generateCoord(false)));
        }
        for (int i = 0; i < nbFull; i++) {
            edges.add(new Edge(generateCoord(true), generateCoord(true)));
        }
        Coord center = generateCoord(centerIsWall);
        Face result = new Face(center, edges);
        result.putProperty(FACE_WALL_KEY, new BooleanType(isWall));
        return result;
    }

    private Coord generateCoord(boolean isWall) {
        Coord result = new Coord(getSeed(), 0);
        result.putProperty(VERTEX_WALL_KEY, new BooleanType(isWall));
        return result;
    }

    @Test
    public void elevateWallsTest() {
        Context context = new Context();
        context.putParam(WALL_HEIGHT_PARAM, 13);
        context.putParam(FLOOR_HEIGHT_PARAM, -1);
        generator.execute(terrainMap, context);
        assertFaceHeight(emptyFace, -1);
        assertFaceHeight(almostEmptyFace, -1);
        assertFaceHeight(fullFace, 13);
        assertFaceHeight(almostFullFace, 13);
    }

    @Test(expected = InvalidAlgorithmParameters.class)
    public void invalidParamsTest() {
        Context context = new Context();
        context.putParam(WALL_HEIGHT_PARAM, 13);
        context.putParam(FLOOR_HEIGHT_PARAM, 100);
        generator.execute(terrainMap, context);
    }

    private void assertFaceHeight(Face face, double height) {
        face.getAllVertices().forEach(
                vertex -> assertThat(vertex.getProperty(HEIGHT_KEY).value, closeTo(height, 0.01))
        );
    }

}
