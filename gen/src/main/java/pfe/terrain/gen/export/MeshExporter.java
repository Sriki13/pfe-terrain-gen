package pfe.terrain.gen.export;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.Face;

import java.util.*;

public class MeshExporter {

    private int size;
    private UUID uuid;

    private List<Face> faces;
    private List<Edge> edges;
    private List<Coord> vertices;

    private Map<Face, Integer> facesMap;
    private Map<Edge, Integer> edgesMap;
    private Map<Coord, Integer> verticesMap;

    public MeshExporter(IslandMap islandMap) {
        this.size = islandMap.getSize();
        this.uuid = UUID.nameUUIDFromBytes(Integer.toString(islandMap.getSeed()).getBytes());
        this.vertices = new ArrayList<>(islandMap.getVertices());
        this.faces = new ArrayList<>(islandMap.getFaces());
        this.edges = new ArrayList<>(islandMap.getEdges());

        this.facesMap = new HashMap<>();
        for (int i = 0; i < faces.size(); i++) {
            facesMap.put(faces.get(i), i);
            this.vertices.add(faces.get(i).getCenter());
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
}
