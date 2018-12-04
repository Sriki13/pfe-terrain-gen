package pfe.terrain.gen.algo;

import org.junit.Test;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.Face;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

public class FaceTest {

    private Coord c1 = new Coord(1, 1);
    private Coord c2 = new Coord(10, 0);
    private Coord c3 = new Coord(9, 8);
    private Coord c4 = new Coord(0, 3);

    private Face myFace = new Face(new Coord(5, 5), Stream.of(
            new Edge(c1, c2),
            new Edge(c2, c3),
            new Edge(c3, c4),
            new Edge(c4, c1)
    ).collect(Collectors.toSet()));

    @Test
    public void testContains() {
        Set<Coord> insidePoints = myFace.getRandomPointsInside(10, new Random());
        for (Coord c : insidePoints) {
            assertThat(c.x, greaterThan(0.0));
            assertThat(c.y, greaterThan(0.0));
            assertThat(c.x, lessThan(10.0));
            assertThat(c.y, lessThan(10.0));
            if (c.x < 3) {
                assertThat(c.y, lessThan(6.0));
            }
            if (c.y > 7) {
                assertThat(c.x, greaterThan(4.5));
            }
        }
    }

    @Test
    public void testTrianglesContains() {
        Set<Coord[]> triangles = myFace.getTriangles();
        for (Coord[] triangle : triangles) {
            System.out.println(Arrays.toString(triangle));
            for (int i = 0; i < 3; i++) {
                System.out.println(Face.getRandomPointInsideTriangle(triangle, new Random()));
            }
            System.out.println();
        }
    }
}
