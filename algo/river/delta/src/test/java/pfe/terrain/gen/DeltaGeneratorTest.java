package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.EdgeSet;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.IntegerType;

import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.DeltaGenerator.DELTA_SOURCE_KEY;
import static pfe.terrain.gen.RiverGenerator.*;
import static pfe.terrain.gen.algo.constraints.Contract.*;

public class DeltaGeneratorTest {

    private DeltaGenerator deltaGenerator;
    private TerrainMap terrainMap;

    private Coord firstDelta;

    private Edge firstNewFlow;
    private Edge secondNewFlow;
    private Edge cantFlow;

    private Edge highFlow;

    private Edge generateEdge(Coord start, Coord end, boolean isRiver) {
        Edge edge = new Edge(start, end);
        if (isRiver) {
            edge.putProperty(RIVER_FLOW_KEY, new IntegerType(1));
        }
        return edge;
    }

    private Coord generateCoord(int seed, double height) {
        Coord coord = new Coord(seed, 0);
        coord.putProperty(HEIGHT_KEY, new DoubleType(height));
        coord.putProperty(VERTEX_WATER_KEY, new BooleanType(false));
        return coord;
    }

    @Before
    public void setUp() {
        deltaGenerator = new DeltaGenerator();
        firstDelta = generateCoord(0, 10);
        firstNewFlow = generateEdge(firstDelta, generateCoord(1, 0), false);
        secondNewFlow = generateEdge(firstDelta, generateCoord(2, 0), false);
        Edge existingRiver = generateEdge(firstDelta, generateCoord(3, 0), true);
        cantFlow = generateEdge(firstDelta, generateCoord(4, 11), false);
        Coord noDeltaTooHigh = generateCoord(5, 100);
        Edge highRiver = generateEdge(noDeltaTooHigh, generateCoord(6, 95), true);
        highFlow = generateEdge(noDeltaTooHigh, generateCoord(7, 95), false);
        EdgeSet allEdges = new EdgeSet(new HashSet<>(Arrays.asList(
                firstNewFlow, secondNewFlow, existingRiver, cantFlow, highFlow, highRiver
        )));
        terrainMap = new TerrainMap();
        terrainMap.putProperty(EDGES, allEdges);
        CoordSet allCoords = new CoordSet(new HashSet<>());
        for (int i = 0; i < 200; i++) {
            allCoords.add(generateCoord(10 + i, 100));
        }
        allEdges.forEach(edge -> {
            allCoords.add(edge.getStart());
            allCoords.add(edge.getEnd());
        });
        terrainMap.putProperty(VERTICES, allCoords);
        terrainMap.putProperty(SEED, 0);
    }

    @Test
    public void spawnDeltasTest() {
        deltaGenerator.execute(terrainMap, new Context());
        for (Coord point : terrainMap.getProperty(VERTICES)) {
            assertThat(point.hasProperty(DELTA_SOURCE_KEY), is(point == firstDelta));
        }
        assertThat(firstNewFlow.hasProperty(RIVER_FLOW_KEY), is(true));
        assertThat(secondNewFlow.hasProperty(RIVER_FLOW_KEY), is(true));
        assertThat(highFlow.hasProperty(RIVER_FLOW_KEY), is(false));
        assertThat(cantFlow.hasProperty(RIVER_FLOW_KEY), is(false));
    }

}
