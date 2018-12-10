package pfe.terrain.gen.algo.export.diff;

import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JsonDiffExporterTest {

    private JsonDiffExporter exporter;

    private JsonObject result;
    private JsonObject original;
    private JsonObject latest;

    @Before
    public void setUp() {
        exporter = new JsonDiffExporter();
        result = new JsonObject();
        original = new JsonObject();
        original.addProperty("samePropAndValue", 0);
        original.addProperty("samePropDifferentValue", 0);
        original.addProperty("removedProp", 0);
        latest = new JsonObject();
        latest.addProperty("samePropAndValue", 0);
        latest.addProperty("samePropDifferentValue", 1);
    }

    @Test
    public void processIslandPropDiffTest() {
        exporter.processIslandDiff(result, original, latest);
        assertThat(result.has("samePropAndValue"), is(false));
        assertThat(result.has("samePropDifferentValue"), is(true));
        assertThat(result.get("samePropDifferentValue").getAsInt(), is(1));
        assertThat(result.has("rm"), is(true));
        assertThat(result.get("rm").getAsJsonArray().size(), is(1));
        assertThat(result.get("rm").getAsJsonArray().get(0).getAsString(), is("removedProp"));
    }

}
