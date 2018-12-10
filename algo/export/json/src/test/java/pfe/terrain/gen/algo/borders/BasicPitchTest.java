package pfe.terrain.gen.algo.borders;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class BasicPitchTest {

    BasicPitch bp;

    private Coord c1 = new Coord(0, 0);
    private Coord c2 = new Coord(0, 10);
    private Coord c3 = new Coord(10, 0);
    private Coord c4 = new Coord(10, 10);

    private Face myFace = new Face(new Coord(5, 5), Stream.of(
            new Edge(c1, c2),
            new Edge(c2, c3),
            new Edge(c3, c4),
            new Edge(c4, c1)
    ).collect(Collectors.toSet()));

    @Before
    public void setUp() throws Exception {
        bp = new BasicPitch();
    }

    @Test
    public void pitch45Degrees() {
        c1.putProperty(BasicPitch.VERTEX_HEIGHT_KEY, new DoubleType(0.0));
        c2.putProperty(BasicPitch.VERTEX_HEIGHT_KEY, new DoubleType(10.0));
        c3.putProperty(BasicPitch.VERTEX_HEIGHT_KEY, new DoubleType(5.0));
        c4.putProperty(BasicPitch.VERTEX_HEIGHT_KEY, new DoubleType(5.0));
        assertThat(bp.computePitch(myFace, 1), equalTo(100.0));
    }

    @Test
    public void pitch0Degrees() {
        c1.putProperty(BasicPitch.VERTEX_HEIGHT_KEY, new DoubleType(0.0));
        c2.putProperty(BasicPitch.VERTEX_HEIGHT_KEY, new DoubleType(0.0));
        c3.putProperty(BasicPitch.VERTEX_HEIGHT_KEY, new DoubleType(0.0));
        c4.putProperty(BasicPitch.VERTEX_HEIGHT_KEY, new DoubleType(0.0));
        assertThat(bp.computePitch(myFace, 1), equalTo(0.0));
    }

    @Test
    public void megaPitch() {
        c1.putProperty(BasicPitch.VERTEX_HEIGHT_KEY, new DoubleType(0.0));
        c2.putProperty(BasicPitch.VERTEX_HEIGHT_KEY, new DoubleType(3.0));
        c3.putProperty(BasicPitch.VERTEX_HEIGHT_KEY, new DoubleType(4.0));
        c4.putProperty(BasicPitch.VERTEX_HEIGHT_KEY, new DoubleType(10.0));
        assertThat(bp.computePitch(myFace, 0.01), greaterThan(200.0));
    }

}
