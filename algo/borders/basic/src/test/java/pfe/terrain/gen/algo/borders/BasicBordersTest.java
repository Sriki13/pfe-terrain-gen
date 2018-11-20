package pfe.terrain.gen.algo.borders;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.geometry.*;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class BasicBordersTest {

    private IslandMap islandMap;
    private BasicBorders basicBorders;

    private List<Coord> validCoords = Arrays.asList(
            new Coord(20, 50), new Coord(10, 86)
    );

    private List<Coord> invalidCoords = Arrays.asList(
            new Coord(0, 10), new Coord(9, 100),
            new Coord(95, 101)
    );

    private Face validFace = new Face(new Coord(10, 10), Arrays.asList(
            new Edge(new Coord(2, 3), new Coord(2, 4)),
            new Edge(new Coord(2, 4), new Coord(3, 4)),
            new Edge(new Coord(3, 4), new Coord(2, 3))
    ));

    private List<Face> invalidFaces = Arrays.asList(
            new Face(new Coord(10, 10), Arrays.asList(
                    new Edge(new Coord(2, 3), new Coord(2, 100)),
                    new Edge(new Coord(2, 100), new Coord(3, 4)),
                    new Edge(new Coord(3, 4), new Coord(2, 3))
            )),
            new Face(new Coord(10, 10), Arrays.asList(
                    new Edge(new Coord(-1, 3), new Coord(2, 4)),
                    new Edge(new Coord(2, 4), new Coord(3, 4)),
                    new Edge(new Coord(3, 4), new Coord(-1, 3))
            )),
            new Face(new Coord(10, 10), Arrays.asList(
                    new Edge(new Coord(2, 3), new Coord(2, 4)),
                    new Edge(new Coord(2, 4), new Coord(3, 0)),
                    new Edge(new Coord(3, 0), new Coord(2, 3))
            ))
    );


    @Before
    public void setUp() throws DuplicateKeyException {
        basicBorders = new BasicBorders();
        islandMap = new IslandMap();
        islandMap.setSize(100);
        List<Coord> allCoords = new ArrayList<>();
        allCoords.addAll(validCoords);
        allCoords.addAll(invalidCoords);
        islandMap.putProperty(new Key<>("VERTICES", CoordSet.class), new CoordSet(allCoords));
        List<Face> allFaces = new ArrayList<>();
        allFaces.add(validFace);
        allFaces.addAll(invalidFaces);
        islandMap.putProperty(new Key<>("FACES", FaceSet.class), new FaceSet(allFaces));
    }

    @Test
    public void generateBordersTest() throws Exception {
        basicBorders.execute(islandMap);
        Set<Coord> borderVertices = new HashSet<>();
        for (Coord vertice : islandMap.getVertices()) {
            if (vertice.getProperty(basicBorders.verticeBorderKey)) {
                borderVertices.add(vertice);
            }
        }
        validCoords.forEach(coord -> assertFalse(borderVertices.contains(coord)));
        invalidCoords.forEach(coord -> assertTrue(borderVertices.contains(coord)));
        Set<Face> borderFaces = new HashSet<>();
        for (Face face : islandMap.getFaces()) {
            if (face.getProperty(basicBorders.faceBorderKey)) {
                borderFaces.add(face);
            }
        }
        assertFalse(borderFaces.contains(validFace));
        invalidFaces.forEach(face -> assertTrue(borderFaces.contains(face)));
    }

    @Test
    public void nameTest() {
        assertThat(basicBorders.getName(), is(basicBorders.getClass().getName()));
    }

}
