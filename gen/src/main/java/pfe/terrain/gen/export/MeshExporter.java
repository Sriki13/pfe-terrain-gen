package pfe.terrain.gen.export;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.*;

import java.util.*;

public class MeshExporter {

    static final Key<CoordSet> verticesKey = new Key<>("VERTICES", CoordSet.class);
    static final Key<EdgeSet> edgesKey = new Key<>("EDGES", EdgeSet.class);
    static final Key<FaceSet> facesKey = new Key<>("FACES", FaceSet.class);
    static final Key<Integer> sizeKey = new Key<>("SIZE", Integer.class);
    static final Key<Integer> seedKey = new Key<>("SEED", Integer.class);

    private int size;
    private UUID uuid;

    private List<Face> faces;
    private List<Edge> edges;
    private List<Coord> vertices;

    private Map<Face, Integer> facesMap;
    private Map<Edge, Integer> edgesMap;
    private Map<Coord, Integer> verticesMap;

    private static final Comparator<Coord> COORD_COMPARATOR =
            (a, b) -> (int) (1000* (((a.x + a.y) - (b.x + b.y))));

    private static final Comparator<Edge> EDGE_COMPARATOR =
            (a, b) -> (int) (1000*((a.getStart().x + a.getStart().y + a.getEnd().x + a.getEnd().y)
                    - (b.getStart().x + b.getStart().y + b.getEnd().x + b.getEnd().y)));

    public MeshExporter(TerrainMap terrainMap) {
        this.size = terrainMap.getProperty(sizeKey);
        this.uuid = UUID.nameUUIDFromBytes(Integer.toString(terrainMap.getProperty(seedKey)).getBytes());
        this.vertices = new ArrayList<>(terrainMap.getProperty(verticesKey));
        this.faces = new ArrayList<>(terrainMap.getProperty(facesKey));
        this.edges = new ArrayList<>(terrainMap.getProperty(edgesKey));

        // Necessary to get deterministic indexes
        vertices.sort(COORD_COMPARATOR);
        faces.sort((a, b) -> COORD_COMPARATOR.compare(a.getCenter(), b.getCenter()));
        edges.sort(EDGE_COMPARATOR);

        this.facesMap = new HashMap<>();
        for (int i = 0; i < faces.size(); i++) {
            facesMap.put(faces.get(i), i);
        }

        this.verticesMap = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++) {
            verticesMap.put(vertices.get(i), i);
        }

        this.edgesMap = new HashMap<>();
        for (int i = 0; i < edges.size(); i++) {
            edgesMap.put(edges.get(i), i);
        }
    }

    public JsonObject export() {
        JsonObject mesh = new JsonObject();
        mesh.addProperty("size", this.size);
        JsonArray verticesArray = new JsonArray(vertices.size());
        for (Coord vertex : vertices) {
            verticesArray.add(coordToArray(vertex));
        }
        mesh.add("vertices", verticesArray);
        JsonArray edgesArray = new JsonArray(edges.size());
        for (Edge edge : edges) {
            edgesArray.add(edgeToArray(edge));
        }
        mesh.add("edges", edgesArray);
        JsonArray facesArray = new JsonArray(faces.size());
        for (Face face : faces) {
            facesArray.add(faceToObject(face));
        }
        mesh.add("faces", facesArray);
        mesh.addProperty("uuid", uuid.toString());
        return mesh;
    }

    private JsonArray coordToArray(Coord coord) {
        JsonArray arr = new JsonArray(2);
        arr.add(coord.x);
        arr.add(coord.y);
        return arr;
    }

    private JsonArray edgeToArray(Edge edge) {
        JsonArray arr = new JsonArray(2);
        arr.add(verticesMap.get(edge.getStart()));
        arr.add(verticesMap.get(edge.getEnd()));
        return arr;
    }

    private JsonObject faceToObject(Face face) {
        JsonObject result = new JsonObject();
        JsonArray neighborsArray = new JsonArray(face.getNeighbors().size());
        face.getNeighbors().forEach(
                neighbor -> neighborsArray.add(facesMap.get(neighbor))
        );
        result.add("neighbors", neighborsArray);
        result.addProperty("center", verticesMap.get(face.getCenter()));
        JsonArray edgesArray = new JsonArray(face.getEdges().size());
        face.getEdges().forEach(
                edge -> edgesArray.add(edgesMap.get(edge))
        );
        result.add("edges", edgesArray);
        return result;
    }

    public Map<Face, Integer> getFacesMap() {
        return facesMap;
    }

    public Map<Edge, Integer> getEdgesMap() {
        return edgesMap;
    }

    public Map<Coord, Integer> getVerticesMap() {
        return verticesMap;
    }

    public boolean sameMesh(MeshExporter other) {
        return facesMap.equals(other.facesMap) && edgesMap.equals(other.edgesMap)
                && verticesMap.equals(other.verticesMap);
    }

}
