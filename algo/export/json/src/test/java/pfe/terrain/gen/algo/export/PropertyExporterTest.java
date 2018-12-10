package pfe.terrain.gen.algo.export;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.IntegerType;
import pfe.terrain.gen.algo.types.OptionalBooleanType;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class PropertyExporterTest {

    private PropertyExporter<Coord> exporter;

    @Before
    public void setUp() {
        Map<Coord, Integer> indexes = new HashMap<>();
        Coord vertexWithProperties = new Coord(0, 0);
        vertexWithProperties.putProperty(new SerializableKey<>("INTERNAL_KEY", "serialized", IntegerType.class), new IntegerType(2));
        vertexWithProperties.putProperty(new SerializableKey<>("INTERNAL_KEY_2", "serialized2", BooleanType.class), new BooleanType(true));
        vertexWithProperties.putProperty(new SerializableKey<>("SERIALIZED BUT FALSE", "serializedButFalse", OptionalBooleanType.class),
                new OptionalBooleanType(false));
        vertexWithProperties.putProperty(new SerializableKey<>("SERIALIZED", "serializedAndTrue", OptionalBooleanType.class),
                new OptionalBooleanType(true));
        vertexWithProperties.putProperty(new Key<>("NOT_SERIALIZED", Integer.class), -1);
        Coord basicVertex = new Coord(1, 1);
        indexes.put(vertexWithProperties, 55);
        indexes.put(basicVertex, 1);
        exporter = new PropertyExporter<>(indexes);
    }

    @Test
    public void propertyExportTest() throws Exception {
        JsonArray propertiesArray = exporter.getPropsArray();
        assertThat(propertiesArray.size(), is(1));
        JsonObject vertexJson = propertiesArray.get(0).getAsJsonObject();
        assertThat(vertexJson.get("key").getAsInt(), is(55));
        JsonArray vals = vertexJson.getAsJsonArray("vals");
        assertThat(vals.size(), is(3));
        for (JsonElement property : vals) {
            JsonObject propertyObject = property.getAsJsonObject();
            String name = propertyObject.get("p").getAsString();
            switch (name) {
                case "serialized":
                    assertThat(propertyObject.get("v").getAsInt(), is(2));
                    break;
                case "serialized2":
                case "serializedAndTrue":
                    assertThat(propertyObject.get("v").getAsBoolean(), is(true));
                    break;
                default:
                    throw new Exception("Unexpected property found: " + name);
            }
        }
    }

}
