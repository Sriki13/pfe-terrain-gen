package pfe.terrain.gen.algo.export.diff;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.export.ExportDiffProcessor;
import pfe.terrain.gen.algo.export.JsonExporter;
import pfe.terrain.gen.algo.island.TerrainMap;

import java.util.Map;

public class JsonExportDiffExporter implements ExportDiffProcessor {

    @Override
    public String processDiff(Contract old, Contract last, TerrainMap terrainMap) {
        if (!(old instanceof JsonExporter) || !(last instanceof JsonExporter)) {
            throw new IllegalArgumentException("Json diff processor can only be used with JsonExporter");
        }
        JsonExporter originalExporter = (JsonExporter) old;
        JsonExporter latestExporter = (JsonExporter) last;
        if (!originalExporter.getMeshExporter().sameMesh(latestExporter.getMeshExporter())) {
            return latestExporter.export(terrainMap);
        }
        JsonObject result = new JsonObject();
        JsonObject original = originalExporter.getLastProduction();
        JsonObject latest = latestExporter.getLastProduction();
        processIslandDiff(result, original, latest);
        processPropertyDiff(result, original, latest, "face");
        processPropertyDiff(result, original, latest, "vertex");
        processPropertyDiff(result, original, latest, "edge");
        return result.toString();
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

    private void processPropertyDiff(JsonObject result, JsonObject original, JsonObject latest,
                                     String propName) {
        PropertyDiffExporter exporter = new PropertyDiffExporter(original.getAsJsonArray(propName + "_props"),
                latest.getAsJsonArray(propName + "_props"));
        JsonArray propDiff = exporter.getDiffArray();
        if (propDiff.size() > 0) {
            result.add(propName + "_props", propDiff);
        }
    }

}
