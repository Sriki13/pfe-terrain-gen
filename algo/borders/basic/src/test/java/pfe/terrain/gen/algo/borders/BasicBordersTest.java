package pfe.terrain.gen.algo.borders;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.geometry.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BasicBordersTest {

    private IslandMap islandMap;
    private BasicBorders basicBorders;

    private List<Coordinate> validCoords = Arrays.asList(
            new Coordinate(20, 50), new Coordinate(10, 86)
    );

    private List<Coordinate> invalidCoords = Arrays.asList(
            new Coordinate(0, 10), new Coordinate(9, 100),
            new Coordinate(95, 101)
    );

    private Face validFace = new Face(new Coordinate(10, 10), Arrays.asList(
            new Edge(new Coordinate(2, 3), new Coordinate(2, 4)),
            new Edge(new Coordinate(2, 4), new Coordinate(3, 4)),
            new Edge(new Coordinate(3, 4), new Coordinate(2, 3))
    ));

    private List<Face> invalidFaces = Arrays.asList(
            new Face(new Coordinate(10, 10), Arrays.asList(
                    new Edge(new Coordinate(2, 3), new Coordinate(2, 100)),
                    new Edge(new Coordinate(2, 100), new Coordinate(3, 4)),
                    new Edge(new Coordinate(3, 4), new Coordinate(2, 3))
            )),
            new Face(new Coordinate(10, 10), Arrays.asList(
                    new Edge(new Coordinate(-1, 3), new Coordinate(2, 4)),
                    new Edge(new Coordinate(2, 4), new Coordinate(3, 4)),
                    new Edge(new Coordinate(3, 4), new Coordinate(-1, 3))
            )),
            new Face(new Coordinate(10, 10), Arrays.asList(
                    new Edge(new Coordinate(2, 3), new Coordinate(2, 4)),
                    new Edge(new Coordinate(2, 4), new Coordinate(3, 0)),
                    new Edge(new Coordinate(3, 0), new Coordinate(2, 3))
            ))
    );


    @Before
    public void setUp() throws DuplicateKeyException {
        basicBorders = new BasicBorders();
        islandMap = new IslandMap();
        islandMap.setSize(100);
        List<Coordinate> allCoords = new ArrayList<>();
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
        BordersSet borders = islandMap.getProperty(new Key<>("BORDERS", BordersSet.class));
        Set<Coordinate> borderVertices = borders.getBorderVertices();
        validCoords.forEach(coord -> assertFalse(borderVertices.contains(coord)));
        invalidCoords.forEach(coord -> assertTrue(borderVertices.contains(coord)));
        Set<Face> borderFaces = borders.getBorderFaces();
        assertFalse(borderFaces.contains(validFace));
        invalidFaces.forEach(face -> assertTrue(borderFaces.contains(face)));
    }

}
