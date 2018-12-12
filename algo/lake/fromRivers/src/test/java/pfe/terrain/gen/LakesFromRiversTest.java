package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.*;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.IntegerType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.LakesFromRivers.HEIGHT_KEY;
import static pfe.terrain.gen.RiverGenerator.*;

public class LakesFromRiversTest {

    private LakesFromRivers lakeGenerator;
    private TerrainMap terrainMap;

    private Face AOB;
    private Face OBC;
    private Face BCD;
    private Face CDE;

    // A
    // |    2
    // O ------ B
    // |  0 / 1 |
    // C ------ D
    // |    1          <- ocean triangle
    // E

    @Before
    public void setUp() {
        terrainMap = new TerrainMap();
        lakeGenerator = new LakesFromRivers();
        CoordSet allCoords = new CoordSet(new HashSet<>());
        EdgeSet allEdges = new EdgeSet(new HashSet<>());
        FaceSet allFaces = new FaceSet(new HashSet<>());
        Coord O = generateCoord(allCoords, 0, -1, true, false);
        Coord A = generateCoord(allCoords, 1, 2, false, false);
        Coord B = generateCoord(allCoords, 2, 1, false, false);
        Coord C = generateCoord(allCoords, 3, 0, false, true);
        Coord D = generateCoord(allCoords, 4, 1, false, true);
        Coord E = generateCoord(allCoords, 5, 1, false, true);
        Edge AB = generateEdge(allEdges, A, B, false);
        Edge AO = generateEdge(allEdges, A, O, true);
        Edge OB = generateEdge(allEdges, O, B, false);
        Edge OC = generateEdge(allEdges, O, C, false);
        Edge BC = generateEdge(allEdges, B, C, false);
        Edge CD = generateEdge(allEdges, C, D, false);
        Edge BD = generateEdge(allEdges, B, D, false);
        Edge CE = generateEdge(allEdges, C, E, false);
        Edge DE = generateEdge(allEdges, D, E, false);
        AOB = generateFace(allFaces, generateCoord(allCoords, 6, 2, false, false),
                asList(AO, OB, AB), WaterKind.NONE);
        OBC = generateFace(allFaces, generateCoord(allCoords, 7, 0, false, false),
                asList(OB, OC, BC), WaterKind.NONE);
        BCD = generateFace(allFaces, generateCoord(allCoords, 8, 1, false, false),
                asList(BC, BD, CD), WaterKind.NONE);
        CDE = generateFace(allFaces, generateCoord(allCoords, 9, 1, false, true),
                asList(CD, CE, DE), WaterKind.OCEAN);
        AOB.addNeighbor(Collections.singleton(OBC));
        OBC.addNeighbor(asList(AOB, BCD));
        BCD.addNeighbor(asList(OBC, CDE));
        CDE.addNeighbor(Collections.singleton(BCD));
        terrainMap.putProperty(Contract.SEED,0);
        terrainMap.putProperty(Contract.VERTICES, allCoords);
        terrainMap.putProperty(Contract.EDGES, allEdges);
        terrainMap.putProperty(Contract.FACES, allFaces);
    }

    private Coord generateCoord(CoordSet allCoords, int seed, double height, boolean isRiverEnd, boolean isWater) {
        Coord result = new Coord(seed, 0);
        allCoords.add(result);
        result.putProperty(HEIGHT_KEY, new DoubleType(height));
        if (isRiverEnd) {
            result.putProperty(IS_RIVER_END_KEY, new MarkerType());
        }
        result.putProperty(VERTEX_WATER_KEY, new BooleanType(isWater));
        return result;
    }

    private Edge generateEdge(EdgeSet allEdges, Coord start, Coord end, boolean isRiver) {
        Edge result = new Edge(start, end);
        allEdges.add(result);
        result.putProperty(RIVER_FLOW_KEY, new IntegerType(isRiver ? 1 : 0));
        return result;
    }

    private Face generateFace(FaceSet allFaces, Coord center, Collection<Edge> edges, WaterKind kind) {
        Face result = new Face(center, new HashSet<>(edges));
        allFaces.add(result);
        result.putProperty(LakesFromRivers.WATER_KIND_KEY, kind);
        result.putProperty(LakesFromRivers.FACE_WATER_KEY, new BooleanType(kind != WaterKind.NONE));
        return result;
    }

    private void assertKind(Face face, WaterKind kind) {
        assertThat(face.getProperty(LakesFromRivers.WATER_KIND_KEY), is(kind));
    }

    private void assertHeight(Coord vertex, double height) {
        assertThat(vertex.getProperty(HEIGHT_KEY).value, closeTo(height, 0.01));
    }

    private void assertFaceHeight(Face face) {
        for (Coord vertex : face.getBorderVertices()) {
            assertHeight(vertex, (double) 0);
        }
        assertHeight(face.getCenter(), (double) 0);
    }

    @Test
    public void createLakeTest() {
        Context context = new Context();
        context.putParam(LakesFromRivers.LAKE_SIZE_PARAM, 1);
        lakeGenerator.execute(terrainMap, context);
        assertKind(AOB, WaterKind.NONE);
        assertKind(OBC, WaterKind.LAKE);
        assertKind(BCD, WaterKind.NONE);
        assertKind(CDE, WaterKind.OCEAN);
        assertFaceHeight(OBC);
    }

    @Test
    public void mergeLakeIntoOceanTest() {
        Context context = new Context();
        context.putParam(LakesFromRivers.LAKE_SIZE_PARAM, 2);
        lakeGenerator.execute(terrainMap, context);
        assertKind(AOB, WaterKind.NONE);
        assertKind(OBC, WaterKind.OCEAN);
        assertKind(BCD, WaterKind.OCEAN);
        assertKind(CDE, WaterKind.OCEAN);
        assertFaceHeight(OBC);
        assertFaceHeight(BCD);
    }

}
