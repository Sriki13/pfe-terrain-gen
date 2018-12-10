package pfe.terrain.gen.algo.export.diff;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PropertyDiffExporterTest {

    private PropertyDiffExporter exporter;
    private JsonArray originalArray;
    private JsonArray lastArray;

    @Before
    public void setUp() {
        originalArray = new JsonArray();
        lastArray = new JsonArray();
        exporter = new PropertyDiffExporter(originalArray, lastArray);
    }

    private JsonObject generateProperty(int key, int value) {
        JsonObject result = new JsonObject();
        JsonArray vals = new JsonArray();
        JsonObject keyAndValue = new JsonObject();
        keyAndValue.addProperty("p", "propName");
        keyAndValue.addProperty("v", value);
        vals.add(keyAndValue);
        result.add("vals", vals);
        result.addProperty("key", key);
        return result;
    }

    private int getKey(JsonArray array, int position) {
        return array.get(position).getAsJsonObject().get("key").getAsInt();
    }

    @Test
    public void addNewPropsTest() {
        originalArray.add(generateProperty(0, 0));
        lastArray.add(generateProperty(0, 0));
        lastArray.add(generateProperty(1, 0));
        JsonArray diff = exporter.getDiffArray();
        assertThat(diff.size(), is(1));
        assertThat(getKey(diff, 0), is(1));
    }

    @Test
    public void removeAProperty() {
        originalArray.add(generateProperty(2, 3));
        originalArray.add(generateProperty(3, 3));
        lastArray.add(generateProperty(1, 2));
        lastArray.add(generateProperty(3, 3));
        JsonArray diff = exporter.getDiffArray();
        assertThat(diff.size(), is(2));
        assertThat(getKey(diff, 0), is(1));
        assertThat(getKey(diff, 1), is(2));
        assertThat(diff.get(1).getAsJsonObject().get("o").getAsString(), is("r"));
    }

    @Test
    public void replaceAProperty() {
        originalArray.add(generateProperty(2, 4));
        originalArray.add(generateProperty(3, 5));
        lastArray.add(generateProperty(2, 5));
        lastArray.add(generateProperty(3, 5));
        JsonArray diff = exporter.getDiffArray();
        assertThat(diff.size(), is(1));
        assertThat(getKey(diff, 0), is(2));
        assertThat(diff.get(0).getAsJsonObject().get("vals")
                        .getAsJsonArray().get(0)
                        .getAsJsonObject().get("v").getAsInt(),
                is(5));
    }

    @Test
    public void moreElementsInOriginal() {
        originalArray.add(generateProperty(1, 0));
        originalArray.add(generateProperty(2, 0));
        originalArray.add(generateProperty(3, 0));
        lastArray.add(generateProperty(4, 0));
        lastArray.add(generateProperty(5, 0));
        JsonArray diff = exporter.getDiffArray();
        assertThat(diff.size(), is(5));
        for (int i = 1; i <= 5; i++) {
            assertThat(getKey(diff, i - 1), is(i));
        }
        for (int i = 1; i <= 3; i++) {
            assertThat(diff.get(i - 1).getAsJsonObject().get("o").getAsString(), is("r"));
        }
    }

}
