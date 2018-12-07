package pfe.terrain.gen.export;

import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.island.geometry.EdgeSet;
import pfe.terrain.gen.algo.island.geometry.FaceSet;

import java.util.HashSet;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class JsonExporterTest {

    private JsonExporter jsonExporter;
    private TerrainMap terrainMap;

    @Before
    public void setUp() throws Exception {
        jsonExporter = new JsonExporter();
        terrainMap = new TerrainMap();
        terrainMap.putProperty(Contract.EDGES, new EdgeSet(new HashSet<>()));
        terrainMap.putProperty(Contract.FACES, new FaceSet(new HashSet<>()));
        terrainMap.putProperty(Contract.VERTICES, new CoordSet(new HashSet<>()));
        terrainMap.putProperty(Contract.SEED, 1);
        terrainMap.putProperty(Contract.SIZE, 100);
    }

    @Test
    public void jsonExportTest() {
        JsonObject result = jsonExporter.export(terrainMap);
        assertThat(result.getAsJsonObject("mesh"), is(notNullValue()));
        assertThat(result.getAsJsonArray("vertex_props"), is(notNullValue()));
        assertThat(result.getAsJsonArray("edge_props"), is(notNullValue()));
        assertThat(result.getAsJsonArray("face_props"), is(notNullValue()));
        assertThat(result.get("uuid"), is(notNullValue()));
    }

}
