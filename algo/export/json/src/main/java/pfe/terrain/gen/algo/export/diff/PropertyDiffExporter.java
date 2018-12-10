package pfe.terrain.gen.algo.export.diff;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PropertyDiffExporter {

    private JsonArray originalProps;
    private JsonArray lastProps;

    public PropertyDiffExporter(JsonArray originalProps, JsonArray lastProps) {
        this.originalProps = originalProps;
        this.lastProps = lastProps;
    }

    public JsonArray getDiffArray() {
        JsonArray propertiesArray = new JsonArray();
        int originalIndex = 0;
        int lastIndex = 0;
        while (originalIndex < originalProps.size() && lastIndex < lastProps.size()) {
            int originalKey = getIntKey(originalProps.get(originalIndex));
            int lastKey = getIntKey(lastProps.get(lastIndex));
            if (lastKey == originalKey) {
                if (!originalProps.get(originalIndex).equals(lastProps.get(lastIndex))) {
                    propertiesArray.add(lastProps.get(lastIndex));
                }
                originalIndex++;
                lastIndex++;
            } else if (lastKey > originalKey) {
                propertiesArray.add(generateRemovalJson(originalKey));
                originalIndex++;
            } else {
                propertiesArray.add(lastProps.get(lastIndex));
                lastIndex++;
            }
        }
        while (lastIndex < lastProps.size()) {
            propertiesArray.add(lastProps.get(lastIndex));
            lastIndex++;
        }
        while (originalIndex < originalProps.size()) {
            propertiesArray.add(generateRemovalJson(
                    getIntKey(originalProps.get(originalIndex))));
            originalIndex++;
        }
        return propertiesArray;
    }

    private int getIntKey(JsonElement keyValue) {
        return keyValue.getAsJsonObject().get("key").getAsInt();
    }

    private JsonObject generateRemovalJson(int key) {
        JsonObject result = new JsonObject();
        result.addProperty("key", key);
        result.addProperty("o", "r");
        return result;
    }

}
