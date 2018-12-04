package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.*;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.RiverGenerator.*;

public class RandomRiversTest {

    private RandomRivers riverGenerator;
    private IslandMap islandMap;

    @Before
    public void setUp() {
        islandMap = new IslandMap();
        riverGenerator = new RandomRivers();
        islandMap.putProperty(Contract.SEED, 0);
        int mapSize = 40;
        Random random = new Random(0);
        CoordSet coords = new CoordSet(new HashSet<>());
        EdgeSet edges = new EdgeSet(new HashSet<>());
        List<Coord> coordsMatrix = new ArrayList<>(Collections.nCopies(mapSize * mapSize, new Coord(0, 0)));
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                Coord coord = new Coord(i, j);
                int lim = 4 + random.nextInt(2);
                if (i < lim || i > mapSize - lim || j < lim || j > mapSize - lim) {
                    coord.putProperty(VERTEX_WATER_KEY, new BooleanType(true));
                } else {
                    coord.putProperty(VERTEX_WATER_KEY, new BooleanType(false));
                }
                int height = random.nextInt(50);
                coord.putProperty(HEIGHT_KEY, new DoubleType(height));
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
        islandMap.putProperty(Contract.VERTICES, coords);
        islandMap.putProperty(Contract.EDGES, edges);
        islandMap.putProperty(Contract.FACES, new FaceSet(new HashSet<>()));
    }

    @Test
    public void generateRiversTest() {
        Context context = new Context();
        int nbRivers = 10;
        context.putParam(RandomRivers.NB_RIVERS_PARAM, nbRivers);
        riverGenerator.execute(islandMap, context);
        Set<Coord> sources = new HashSet<>();
        for (Coord coord : islandMap.getVertices()) {
            if (coord.hasProperty(IS_SOURCE_KEY)) {
                sources.add(coord);
            }
        }
        assertThat(sources.size(), greaterThanOrEqualTo(nbRivers));
        for (Coord source : sources) {
            Coord start = source;
            Edge before = null;
            while (!start.hasProperty(IS_RIVER_END_KEY)) {
                List<Edge> next = findRiverEdge(start, before);
                assertThat(next.size(), is(1));
                before = next.get(0);
                Coord cmp = (start == before.getStart() ? before.getEnd() : before.getStart());
                assertThat(cmp.getProperty(HEIGHT_KEY).value, lessThan(start.getProperty(HEIGHT_KEY).value));
                start = cmp;
            }
        }
    }

    private List<Edge> findRiverEdge(Coord coord, Edge ignore) {
        List<Edge> result = new ArrayList<>();
        for (Edge edge : islandMap.getEdges()) {
            if (edge.hasProperty(RIVER_FLOW_KEY) && edge.getProperty(RIVER_FLOW_KEY).value > 0
                    && (edge.getStart() == coord || edge.getEnd() == coord)
                    && edge != ignore) {
                result.add(edge);
            }
        }
        return result;
    }


}
