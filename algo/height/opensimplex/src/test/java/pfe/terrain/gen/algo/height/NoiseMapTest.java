package pfe.terrain.gen.algo.height;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.algorithms.HeightGenerator;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class NoiseMapTest {

    private Set<Coord> vertices;
    private NoiseMap noiseMap;

    @Before
    public void setUp() throws Exception {
        vertices = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Coord vertex = new Coord(i, j);
                if (isBorder(vertex)) {
                    vertex.putProperty(HeightGenerator.verticeBorderKey, new BooleanType(true));
                } else {
                    vertex.putProperty(HeightGenerator.verticeBorderKey, new BooleanType(false));
                }
                vertices.add(vertex);
            }
        }
        noiseMap = new NoiseMap(vertices, 0);
    }

    private boolean isBorder(Coord vertex) {
        return vertex.x == 0 || vertex.y == 0 || vertex.x == 9 || vertex.y == 9;
    }

    @Test
    public void addSimplexTest() throws Exception {
        noiseMap.addSimplexNoise(0.7, 0.05);
        noiseMap.addSimplexNoise(0.35, 0.025);
        noiseMap.addSimplexNoise(0.175, 0.0125);
        //noiseMap.redistribute(3);
        // the values are random, all we can test is that they are generated
        noiseMap.putValuesInRange();
        noiseMap.ensureBordersAreLow();
        noiseMap.putHeightProperty();
        for (Coord vertex : vertices) {
            double value = vertex.getProperty(HeightGenerator.vertexHeightKey).value;
            assertThat(value, is(greaterThanOrEqualTo(0.0)));
            assertThat(value, is(lessThanOrEqualTo(20.0)));
            if (isBorder(vertex)) {
                assertThat(value, closeTo(0.0, 0.01));
            }
        }
    }

}
