package pfe.terrain.gen.export;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.geometry.*;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MeshExporterTest {

    private IslandMap islandMap;
    private MeshExporter meshExporter;

    private Edge commonInFaces = new Edge(new Coord(0, 5), new Coord(5, 8));

    private Coord firstFaceCenter = new Coord(1, 1);
    private Coord firstFaceLastPoint = new Coord(3, 4);
    private List<Edge> firstFaceEdges = Arrays.asList(
            commonInFaces,
            new Edge(commonInFaces.getEnd(), firstFaceLastPoint),
            new Edge(firstFaceLastPoint, commonInFaces.getStart())
    );
    private Face firstFace = new Face(firstFaceCenter, firstFaceEdges);

    private Coord secondFaceCenter = new Coord(2, 2);
    private Coord secondFaceLastPoint = new Coord(8, 9);
    private List<Edge> secondFaceEdges = Arrays.asList(
            commonInFaces,
            new Edge(commonInFaces.getEnd(), secondFaceLastPoint),
            new Edge(secondFaceLastPoint, commonInFaces.getStart())
    );
    private Face secondFace = new Face(secondFaceCenter, secondFaceEdges);

    private Edge loneEdge = new Edge(new Coord(8, 8), new Coord(9, 9));


    private List<Coord> vertices = Arrays.asList(
            commonInFaces.getStart(), commonInFaces.getEnd(), firstFaceLastPoint, secondFaceLastPoint,
            loneEdge.getStart(), loneEdge.getEnd()
    );

    @Before
    public void setUp() throws Exception {
        islandMap = new IslandMap();
        islandMap.putProperty(new Key<>("SIZE", Integer.class), 100);
        islandMap.putProperty(new Key<>("VERTICES", CoordSet.class), new CoordSet(vertices));
        List<Edge> allEdges = new ArrayList<>(firstFaceEdges);
        allEdges.addAll(secondFaceEdges);
        allEdges.add(loneEdge);
        islandMap.putProperty(new Key<>("EDGES", EdgeSet.class), new EdgeSet(allEdges));
        firstFace.addNeighbor(secondFace);
        secondFace.addNeighbor(firstFace);
        islandMap.putProperty(new Key<>("FACES", FaceSet.class), new FaceSet(Arrays.asList(
                firstFace, secondFace
        )));
        meshExporter = new MeshExporter(islandMap);
    }

    @Test
    public void meshExportTest() throws Exception {
        JsonObject json = meshExporter.export();
        assertThat(json.get("size").getAsInt(), is(islandMap.getSize()));
        checkVertices(json);
        checkEdges(json, meshExporter.getVerticesMap());
        checkFaces(json);
    }

    private void checkVertices(JsonObject json) throws Exception {
        JsonArray verticesArray = json.getAsJsonArray("vertices");
        assertThat(verticesArray.size(), is(vertices.size() + 2));
        for (Coord coord : vertices) {
            checkCoordInJsonArray(coord, verticesArray);
        }
        checkCoordInJsonArray(firstFaceCenter, verticesArray);
        validateVertexMap(verticesArray, meshExporter.getVerticesMap());
    }

    private void checkCoordInJsonArray(Coord coord, JsonArray verticesArray) throws Exception {
        boolean found = false;
        for (JsonElement element : verticesArray) {
            JsonArray arr = element.getAsJsonArray();
            int[] numbers = new int[2];
            numbers[0] = arr.get(0).getAsInt();
            numbers[1] = arr.get(1).getAsInt();
            if ((coord.x == numbers[0] && coord.y == numbers[1]) || (coord.x == numbers[1] && coord.y == numbers[0])) {
                if (found) {
                    throw new Exception("Duplicate elements in json export");
                }
                found = true;
            }
        }
        assertThat(found, is(true));
    }

    private void validateVertexMap(JsonArray verticesArray, Map<Coord, Integer> map) {
        assertThat(map.size(), is(verticesArray.size()));
        for (int i = 0; i < verticesArray.size(); i++) {
            JsonArray arr = verticesArray.get(i).getAsJsonArray();
            Coord got = new Coord(arr.get(0).getAsInt(), arr.get(1).getAsInt());
            assertThat(map.get(got), is(i));
        }
    }

    private void checkEdges(JsonObject json, Map<Coord, Integer> verticeMap) throws Exception {
        JsonArray edgesArray = json.getAsJsonArray("edges");
        JsonArray verticesArray = json.getAsJsonArray("vertices");
        assertThat(edgesArray.size(), is(firstFaceEdges.size() + secondFaceEdges.size()));
        checkEdgesInJsonArray(firstFaceEdges, edgesArray, verticeMap);
        checkEdgesInJsonArray(Collections.singletonList(loneEdge), edgesArray, verticeMap);
        validateEdgeMap(edgesArray, verticesArray, meshExporter.getEdgesMap());
    }

    private void checkEdgesInJsonArray(List<Edge> edges, JsonArray edgesArray, Map<Coord, Integer> verticeMap)
            throws Exception {
        for (Edge edge : edges) {
            Coord expected = new Coord(verticeMap.get(edge.getStart()), verticeMap.get(edge.getEnd()));
            checkCoordInJsonArray(expected, edgesArray);
        }
    }

    private void validateEdgeMap(JsonArray edgesArray, JsonArray verticesArray, Map<Edge, Integer> edgesMap) {
        assertThat(edgesMap.size(), is(edgesArray.size()));
        for (int i = 0; i < edgesArray.size(); i++) {
            JsonArray arr = edgesArray.get(i).getAsJsonArray();
            Edge got = arrToEdge(arr, verticesArray);
            assertThat(edgesMap.get(got), is(i));
        }
    }

    private Edge arrToEdge(JsonArray arr, JsonArray verticesArray) {
        JsonArray start = verticesArray.get(arr.get(0).getAsInt()).getAsJsonArray();
        JsonArray end = verticesArray.get(arr.get(1).getAsInt()).getAsJsonArray();
        return new Edge(new Coord(start.get(0).getAsInt(), start.get(1).getAsInt()),
                new Coord(end.get(0).getAsInt(), end.get(1).getAsInt()));
    }

    private void checkFaces(JsonObject json) {
        JsonArray verticesArray = json.getAsJsonArray("vertices");
        JsonArray facesArray = json.getAsJsonArray("faces");
        JsonArray edgesArray = json.getAsJsonArray("edges");
        assertThat(facesArray.size(), is(2));
        checkFaceInArray(firstFace, facesArray, edgesArray, verticesArray);
        checkFaceInArray(secondFace, facesArray, edgesArray, verticesArray);
        assertThat(meshExporter.getFacesMap().size(), is(2));
    }

    private void checkFaceInArray(Face face, JsonArray facesArray, JsonArray edgesArray, JsonArray verticesArray) {
        boolean found = false;
        for (int i = 0; i < facesArray.size(); i++) {
            JsonElement element = facesArray.get(i);
            JsonObject faceObject = element.getAsJsonObject();
            int centerId = faceObject.get("center").getAsInt();
            if (centerId != meshExporter.getVerticesMap().get(face.getCenter())) {
                continue;
            }
            found = true;
            JsonArray faceEdges = faceObject.getAsJsonArray("edges");
            assertThat(faceEdges.size(), is(face.getEdges().size()));
            for (JsonElement edgeInJson : faceEdges) {
                int edgeId = edgeInJson.getAsInt();
                Edge got = arrToEdge(edgesArray.get(edgeId).getAsJsonArray(), verticesArray);
                assertTrue(face.getEdges().contains(got));
            }
            JsonArray neighbors = faceObject.getAsJsonArray("neighbors");
            assertThat(neighbors.size(), is(face.getNeighbors().size()));
            for (JsonElement neighbor : neighbors) {
                int expected = (i == 0 ? 1 : 0);
                assertThat(neighbor.getAsInt(), is(expected));
            }
            break;
        }
        assertThat(found, is(true));
    }

}
