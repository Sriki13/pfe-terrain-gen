package pfe.terrain.gen.algo.height;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SimplexNoiseMapTest {

    public static final int TEST_SIZE = 50;

    private Set<Coord> vertices;
    private SimplexNoiseMap simplexNoiseMap;

    @Before
    public void setUp() {
        vertices = new HashSet<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            for (int j = 0; j < TEST_SIZE; j++) {
                Coord vertex = new Coord(i, j);
                if (isBorder(vertex)) {
                    vertex.putProperty(SimplexHeight.VERTEX_BORDER_KEY, new MarkerType());
                }
                vertices.add(vertex);
            }
        }
        simplexNoiseMap = new SimplexNoiseMap(vertices, 1600, 0);
    }

    private boolean isBorder(Coord vertex) {
        return vertex.x == 0 || vertex.y == 0 || vertex.x == TEST_SIZE - 1 || vertex.y == TEST_SIZE - 1;
    }

    @Test
    public void addSimplexTest() {
        simplexNoiseMap.addSimplexNoise(0.7, 0.05, 0, SimplexIslandShape.CIRCLE);
        simplexNoiseMap.addSimplexNoise(0.35, 0.025, 0, SimplexIslandShape.CIRCLE);
        simplexNoiseMap.addSimplexNoise(0.175, 0.0125, 0, SimplexIslandShape.CIRCLE);
        // the values are random, all we can test is that they are generated
        simplexNoiseMap.ensureBordersAreLow();
        simplexNoiseMap.putHeightProperty();
        for (Coord vertex : vertices) {
            double value = vertex.getProperty(SimplexHeight.VERTEX_HEIGHT_KEY).value;
            assertThat(value, notNullValue());
            if (isBorder(vertex)) {
                assertThat(value, lessThanOrEqualTo(0.0));
            }
        }
    }

}
