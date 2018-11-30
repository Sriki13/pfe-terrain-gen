package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.*;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.RiverGenerator.*;

public class RandomRiversTest {

    private RandomRivers riverGenerator;
    private IslandMap islandMap;

    @Before
    public void setUp() {
        islandMap = new IslandMap();
        riverGenerator = new RandomRivers();
        islandMap.putProperty(Contract.seed, 0);
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
                    coord.putProperty(vertexWaterKey, new BooleanType(true));
                } else {
                    coord.putProperty(vertexWaterKey, new BooleanType(false));
                }
                int height = random.nextInt(50);
                coord.putProperty(heightKey, new DoubleType(height));
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
        islandMap.putProperty(Contract.vertices, coords);
        islandMap.putProperty(Contract.edges, edges);
        islandMap.putProperty(Contract.faces, new FaceSet(new HashSet<>()));
    }

    @Test
    public void generateRiversTest() {
        Context context = new Context();
        int nbRivers = 10;
        context.putParam(RandomRivers.nbRiversParam, nbRivers);
        riverGenerator.execute(islandMap, context);
        for (Coord vertex : islandMap.getVertices()) {
            assertThat(vertex.getProperty(isSourceKey), notNullValue());
            assertThat(vertex.getProperty(isRiverEndKey), notNullValue());
        }
        Set<Coord> sources = new HashSet<>();
        for (Coord coord : islandMap.getVertices()) {
            if (coord.getProperty(isSourceKey)) {
                sources.add(coord);
            }
        }
        assertThat(sources.size(), greaterThanOrEqualTo(nbRivers));
        for (Coord source : sources) {
            Coord start = source;
            Edge before = null;
            while (!start.getProperty(isRiverEndKey)) {
                List<Edge> next = findRiverEdge(start, before);
                assertThat(next.size(), is(1));
                before = next.get(0);
                Coord cmp = (start == before.getStart() ? before.getEnd() : before.getStart());
                assertThat(cmp.getProperty(heightKey).value, lessThan(start.getProperty(heightKey).value));
                start = cmp;
            }
        }
    }

    private List<Edge> findRiverEdge(Coord coord, Edge ignore) {
        List<Edge> result = new ArrayList<>();
        for (Edge edge : islandMap.getEdges()) {
            if (edge.getProperty(riverFlowKey).value > 0
                    && (edge.getStart() == coord || edge.getEnd() == coord)
                    && edge != ignore) {
                result.add(edge);
            }
        }
        return result;
    }


}
