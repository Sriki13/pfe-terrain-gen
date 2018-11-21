package pfe.terrain.gen.export;

import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.geometry.EdgeSet;
import pfe.terrain.gen.algo.geometry.FaceSet;

import java.util.HashSet;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class JSONExporterTest {

    private JSONExporter jsonExporter;
    private IslandMap islandMap;

    @Before
    public void setUp() throws Exception {
        jsonExporter = new JSONExporter();
        islandMap = new IslandMap();
        islandMap.putProperty(Contract.edges, new EdgeSet(new HashSet<>()));
        islandMap.putProperty(Contract.faces, new FaceSet(new HashSet<>()));
        islandMap.putProperty(Contract.vertices, new CoordSet(new HashSet<>()));
        islandMap.putProperty(Contract.size, 100);
    }

    @Test
    public void jsonExportTest() {
        JsonObject result = jsonExporter.export(islandMap);
        assertThat(result.getAsJsonObject("mesh"), is(notNullValue()));
        assertThat(result.getAsJsonArray("vertex_props"), is(notNullValue()));
        assertThat(result.getAsJsonArray("edge_props"), is(notNullValue()));
        assertThat(result.getAsJsonArray("face_props"), is(notNullValue()));
    }

}
