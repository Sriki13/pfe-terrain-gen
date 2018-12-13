package pfe.terrain.gen.algo.export.diff;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pfe.terrain.gen.algo.export.JsonExporter;
import pfe.terrain.gen.algo.export.MeshExporter;
import pfe.terrain.gen.algo.island.TerrainMap;

import java.util.Map;

public class JsonDiffExporter {

    private MeshExporter lastMesh;
    private JsonObject lastMap;

    public JsonObject processDiff(JsonObject oldMap, MeshExporter oldMesh, TerrainMap terrainMap) {
        JsonExporter latestExporter = new JsonExporter();
        this.lastMap = latestExporter.export(terrainMap);
        this.lastMesh = latestExporter.getMeshExporter();
        if (!latestExporter.getMeshExporter().sameMesh(oldMesh)) {
            return lastMap;
        }
        JsonObject result = new JsonObject();
        processIslandDiff(result, oldMap, lastMap);
        processPropertyDiff(result, oldMap, lastMap, "face");
        processPropertyDiff(result, oldMap, lastMap, "vertex");
        processPropertyDiff(result, oldMap, lastMap, "edge");
        return result;
    }

    public MeshExporter getLastMesh() {
        return lastMesh;
    }

    public JsonObject getLastMap() {
        return lastMap;
    }

    public void processIslandDiff(JsonObject result, JsonObject original, JsonObject latest) {
        for (Map.Entry<String, JsonElement> entry : latest.entrySet()) {
            if (!original.has(entry.getKey()) || !original.get(entry.getKey()).equals(entry.getValue())) {
                result.add(entry.getKey(), entry.getValue());
            }
        }
        JsonArray removal = new JsonArray();
        for (Map.Entry<String, JsonElement> entry : original.entrySet()) {
            if (!latest.has(entry.getKey())) {
                removal.add(entry.getKey());
            }
        }
        if (removal.size() > 0) {
            result.add("rm", removal);
        }
    }

    private void processPropertyDiff(JsonObject result, JsonObject original, JsonObject latest, String propName) {
        PropertyDiffExporter exporter = new PropertyDiffExporter(original.getAsJsonArray(propName + "_props"),
                latest.getAsJsonArray(propName + "_props"));
        JsonArray propDiff = exporter.getDiffArray();
        if (propDiff.size() > 0) {
            result.add(propName + "_props", propDiff);
        }
    }

}
