package pfe.terrain.gen.export;

import com.google.gson.JsonObject;
import pfe.terrain.gen.algo.IslandMap;

import java.util.UUID;

public class JSONExporter {

    public JsonObject export(IslandMap islandMap) {
        JsonObject result = new JsonObject();
        MeshExporter meshExporter = new MeshExporter(islandMap);
        result.add("mesh", meshExporter.export());
        PropertyExporter vertexExporter = new PropertyExporter<>(meshExporter.getVerticesMap());
        result.add("vertex_props", vertexExporter.getPropsArray());
        PropertyExporter edgeExporter = new PropertyExporter<>(meshExporter.getEdgesMap());
        result.add("edge_props", edgeExporter.getPropsArray());
        PropertyExporter faceExporter = new PropertyExporter<>(meshExporter.getFacesMap());
        result.add("face_props", faceExporter.getPropsArray());
        result.addProperty("uuid", UUID.nameUUIDFromBytes(
                Integer.toString(islandMap.getSeed()).getBytes()).toString());
        return result;
    }

}
