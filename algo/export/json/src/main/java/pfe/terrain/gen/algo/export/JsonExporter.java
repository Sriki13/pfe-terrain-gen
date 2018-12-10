package pfe.terrain.gen.algo.export;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.types.SerializableType;

import java.util.Map;

public class JsonExporter {

    private Key<Integer> seedKey = new Key<>("SEED", Integer.class);

    private MeshExporter meshExporter;

    public MeshExporter getMeshExporter() {
        return meshExporter;
    }

    public JsonObject export(TerrainMap map) {
        JsonObject result = new JsonObject();

        meshExporter = new MeshExporter(map);
        result.add("mesh", meshExporter.export());

        PropertyExporter vertexExporter = new PropertyExporter<>(meshExporter.getVerticesMap());
        result.add("vertex_props", vertexExporter.getPropsArray());
        PropertyExporter edgeExporter = new PropertyExporter<>(meshExporter.getEdgesMap());
        result.add("edge_props", edgeExporter.getPropsArray());

        PropertyExporter faceExporter = new PropertyExporter<>(meshExporter.getFacesMap());
        result.add("face_props", faceExporter.getPropsArray());

        Map<Key<?>, Object> properties = map.getProperties();
        for (Key key : properties.keySet()) {
            if (key.isSerialized() && properties.get(key) instanceof SerializableType) {
                JsonElement serialized = ((SerializableType) properties.get(key)).serialize();
                if (serialized != null) {
                    result.add(key.getSerializedName(), serialized);
                }
            }
        }

        result.addProperty("uuid", map.getProperty(seedKey));
        return result;
    }

}
