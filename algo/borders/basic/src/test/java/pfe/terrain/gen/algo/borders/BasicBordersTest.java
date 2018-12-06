package pfe.terrain.gen.algo.borders;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static pfe.terrain.gen.algo.constraints.Contract.FACES;
import static pfe.terrain.gen.algo.constraints.Contract.VERTICES;

public class BasicBordersTest {

    private TerrainMap terrainMap;
    private BasicBorders basicBorders;

    private List<Coord> validCoords = Arrays.asList(
            new Coord(20, 50), new Coord(11, 86)
    );

    private List<Coord> invalidCoords = Arrays.asList(
            new Coord(0, 10), new Coord(9, 100),
            new Coord(95, 101)
    );

    private Face validFace = new Face(new Coord(10, 10), Stream.of(
            new Edge(new Coord(20, 30), new Coord(20, 40)),
            new Edge(new Coord(20, 40), new Coord(30, 40)),
            new Edge(new Coord(30, 40), new Coord(20, 30))
    ).collect(Collectors.toSet()));

    private List<Face> invalidFaces = Arrays.asList(
            new Face(new Coord(10, 10), Stream.of(
                    new Edge(new Coord(2, 3), new Coord(2, 100)),
                    new Edge(new Coord(2, 100), new Coord(3, 4)),
                    new Edge(new Coord(3, 4), new Coord(2, 3))
            ).collect(Collectors.toSet())),
            new Face(new Coord(10, 10), Stream.of(
                    new Edge(new Coord(-1, 3), new Coord(2, 4)),
                    new Edge(new Coord(2, 40), new Coord(3, 40)),
                    new Edge(new Coord(3, 4), new Coord(-1, 3))
            ).collect(Collectors.toSet())),
            new Face(new Coord(10, 10), Stream.of(
                    new Edge(new Coord(20, 3), new Coord(2, 4)),
                    new Edge(new Coord(2, 4), new Coord(3, 0)),
                    new Edge(new Coord(3, 0), new Coord(20, 3))
            ).collect(Collectors.toSet()))
    );


    @Before
    public void setUp() throws DuplicateKeyException {
        basicBorders = new BasicBorders();
        terrainMap = new TerrainMap();
        terrainMap.putProperty(Contract.SIZE, 100);
        List<Coord> allCoords = new ArrayList<>();
        allCoords.addAll(validCoords);
        allCoords.addAll(invalidCoords);
        terrainMap.putProperty(new Key<>("VERTICES", CoordSet.class), new CoordSet(allCoords));
        List<Face> allFaces = new ArrayList<>();
        allFaces.add(validFace);
        allFaces.addAll(invalidFaces);
        terrainMap.putProperty(new Key<>("FACES", FaceSet.class), new FaceSet(allFaces));
    }

    @Test
    public void generateBordersTest() {
        basicBorders.execute(terrainMap, new Context());
        Set<Coord> borderVertices = new HashSet<>();
        for (Coord vertice : terrainMap.getProperty(VERTICES)) {
            if (vertice.hasProperty(basicBorders.VERTICE_BORDER_KEY)) {
                borderVertices.add(vertice);
            }
        }
        validCoords.forEach(coord -> assertFalse(borderVertices.contains(coord)));
        invalidCoords.forEach(coord -> assertTrue(borderVertices.contains(coord)));
        Set<Face> borderFaces = new HashSet<>();
        for (Face face : terrainMap.getProperty(FACES)) {
            if (face.hasProperty(basicBorders.FACE_BORDER_KEY)) {
                borderFaces.add(face);
            }
        }
        assertFalse(borderFaces.contains(validFace));
        invalidFaces.forEach(face -> assertTrue(borderFaces.contains(face)));
    }

    @Test
    public void nameTest() {
        assertThat(basicBorders.getName(), is(basicBorders.getClass().getSimpleName()));
    }

}
