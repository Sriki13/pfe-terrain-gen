package pfe.terrain.gen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pfe.terrain.gen.algo.constraints.Contract.EDGES;
import static pfe.terrain.gen.algo.constraints.Contract.FACES;
import static pfe.terrain.gen.algo.constraints.Contract.VERTICES;

public class MeshBuilderTest {

    private TerrainMap map;

    @Before
    public void initMapWithGrid() throws Exception {
        generateMap(20);
    }

    private void generateMap(int size) throws Exception {
        this.map = new TerrainMap();

        CoordSet points = new CoordSet();

        for (double i = 0; i < size; i += 1) {
            for (double j = 0; j < size; j += 1) {
                points.add(new Coord(i, j));
            }
        }
        map.putProperty(new Key<>("SIZE", Integer.class), 20);
        map.putProperty(new Key<>("POINTS", CoordSet.class), points);
    }

    @Test
    public void EdgeVerticesTest() throws Exception {
        MeshBuilder builder = new MeshBuilder();
        builder.execute(map, new Context());
        CoordSet vertices = map.getProperty(VERTICES);
        EdgeSet edges = map.getProperty(EDGES);

        CoordSet verticesEdge = new CoordSet();

        for (Edge edge : edges) {
            verticesEdge.add(edge.getEnd());
            verticesEdge.add(edge.getStart());
        }
        assertTrue(vertices.containsAll(verticesEdge));
    }

    @Test
    public void FaceVerticesTest() throws Exception {
        MeshBuilder builder = new MeshBuilder();
        builder.execute(map, new Context());
        FaceSet faces = map.getProperty(FACES);
        CoordSet verticesFace = new CoordSet();
        EdgeSet edgesFace = new EdgeSet();
        for (Face face : faces) {
            verticesFace.add(face.getCenter());
            verticesFace.addAll(face.getBorderVertices());
            edgesFace.addAll(face.getEdges());
        }
        assertTrue(map.getProperty(VERTICES).containsAll(verticesFace));
        assertTrue(map.getProperty(EDGES).containsAll(edgesFace));
    }

    @Test
    public void samePointsEdgeTest() throws Exception {
        MeshBuilder builder = new MeshBuilder();
        builder.execute(map, new Context());
        EdgeSet edges = map.getProperty(EDGES);
        for (Edge edge : edges) {
            Assert.assertNotEquals("Should be different " + edge.getEnd() + " " + edge.getStart(), edge.getEnd(), edge.getStart());
        }
    }

    @Test
    public void sameEdgeDifferentWay() throws Exception {
        MeshBuilder builder = new MeshBuilder();
        builder.execute(map, new Context());
        EdgeSet edges = map.getProperty(EDGES);
        List<Edge> edgesList = new ArrayList<>(edges);
        for (int i = 0; i < edgesList.size(); i++) {
            for (int j = i + 1; j < edgesList.size(); j++) {
                assertFalse((edgesList.get(i).getEnd().equals(edgesList.get(j).getStart()))
                        && (edgesList.get(i).getStart().equals(edgesList.get(j).getEnd())));
            }
        }
    }

    @Test
    public void allEdgesAreDifferent() throws Exception {
        MeshBuilder builder = new MeshBuilder();
        builder.execute(map, new Context());
        List<Edge> edges = new ArrayList<>(map.getProperty(EDGES));
        for (Edge edge : map.getProperty(EDGES)) {
            edges.remove(edge);
            Assert.assertFalse(edges.contains(edge));
        }
    }

    @Test
    public void allElementsPassedAsReference() throws Exception {
        generateMap(5);
        MeshBuilder builder = new MeshBuilder();
        builder.execute(map, new Context());
        for (Edge edge : map.getProperty(EDGES)) {
            assertTrue(findCoordInVertices(edge.getStart()));
            assertTrue(findCoordInVertices(edge.getEnd()));
        }
        for (Face face : map.getProperty(FACES)) {
            for (Coord vertex : face.getBorderVertices()) {
                assertTrue(findCoordInVertices(vertex));
            }
            for (Edge edge : face.getEdges()) {
                assertTrue(findEdge(edge));
            }
        }
    }

    @Test
    public void facesContainsAllVertices() throws Exception {
        generateMap(5);
        MeshBuilder builder = new MeshBuilder();
        builder.execute(map, new Context());
        CoordSet verticesFromFace = new CoordSet();
        for (Face face : map.getProperty(FACES)) {
            verticesFromFace.addAll(face.getBorderVertices());
            verticesFromFace.add(face.getCenter());
        }
        for (Coord coord : verticesFromFace) {
            assertTrue(findCoordInVertices(coord));
        }
        for (Coord coord : map.getProperty(VERTICES)) {
            assertTrue(findCoordInVertices(coord, verticesFromFace));
        }
        assertTrue(map.getProperty(VERTICES).containsAll(verticesFromFace));
        assertTrue(verticesFromFace.containsAll(map.getProperty(VERTICES)));
    }

    private boolean findCoordInVertices(Coord coord) {
        for (Coord vertex : map.getProperty(VERTICES)) {
            if (coord == vertex) {
                return true;
            }
        }
        return false;
    }

    private boolean findCoordInVertices(Coord coord, CoordSet mySet) {
        for (Coord vertex : mySet) {
            if (coord == vertex) {
                return true;
            }
        }
        return false;
    }

    private boolean findEdge(Edge search) {
        for (Edge edge : map.getProperty(EDGES)) {
            if (edge == search) {
                return true;
            }
        }
        return false;
    }
}
