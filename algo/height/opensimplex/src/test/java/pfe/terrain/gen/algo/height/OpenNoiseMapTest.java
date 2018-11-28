package pfe.terrain.gen.algo.height;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class OpenNoiseMapTest {

    public static final int TEST_SIZE = 50;

    private Set<Coord> vertices;
    private OpenNoiseMap openNoiseMap;

    @Before
    public void setUp() throws Exception {
        vertices = new HashSet<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            for (int j = 0; j < TEST_SIZE; j++) {
                Coord vertex = new Coord(i, j);
                if (isBorder(vertex)) {
                    vertex.putProperty(OpenSimplexHeight.vertexBorderKey, new BooleanType(true));
                } else {
                    vertex.putProperty(OpenSimplexHeight.vertexBorderKey, new BooleanType(false));
                }
                vertices.add(vertex);
            }
        }
        openNoiseMap = new OpenNoiseMap(vertices, 0, 1600);
    }

    private boolean isBorder(Coord vertex) {
        return vertex.x == 0 || vertex.y == 0 || vertex.x == TEST_SIZE - 1 || vertex.y == TEST_SIZE - 1;
    }

    @Test
    public void addSimplexTest() throws Exception {
        openNoiseMap.addSimplexNoise(0.7, 0.05);
        openNoiseMap.addSimplexNoise(0.35, 0.025);
        openNoiseMap.addSimplexNoise(0.175, 0.0125);
        // the values are random, all we can test is that they are generated
        openNoiseMap.putValuesInRange(4);
        openNoiseMap.ensureBordersAreLow();
        openNoiseMap.putHeightProperty();
        for (Coord vertex : vertices) {
            double value = vertex.getProperty(OpenSimplexHeight.vertexHeightKey).value;
            assertThat(value, notNullValue());
            if (isBorder(vertex)) {
                assertThat(value, lessThanOrEqualTo(0.0));
            }
        }
    }

}