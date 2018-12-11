package pfe.terrain.gen.algo.export;

import com.google.gson.JsonObject;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.PermanentKey;
import pfe.terrain.gen.algo.export.diff.JsonDiffExporter;
import pfe.terrain.gen.algo.island.TerrainMap;

import java.util.Set;

public class JsonExportContract extends Contract {

    public static final Param<Boolean> DIFF_PARAM = new Param<>("jsonDiff", Boolean.class, "true / false",
            "Whether the produced json should only be the diff with the earlier excution or not",
            false, "Enable diff Json");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(DIFF_PARAM);
    }

    public static final Key<Void> ALL_KEY = new Key<>("All", Void.class);

    public static final Key<JsonObject> EXPORT_JSON_KEY = new PermanentKey<>("json", JsonObject.class);
    public static final Key<MeshExporter> EXPORT_MESH_KEY = new PermanentKey<>("MESH_EXPORTER", MeshExporter.class);

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(ALL_KEY), asKeySet(EXPORT_JSON_KEY, EXPORT_MESH_KEY));
    }

    @Override
    public String getDescription() {
        return "A Json exporter for island maps.";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        if (!map.hasProperty(EXPORT_JSON_KEY) || !context.getParamOrDefault(DIFF_PARAM)) {
            JsonExporter exporter = new JsonExporter();
            map.putProperty(EXPORT_JSON_KEY, exporter.export(map));
            map.putProperty(EXPORT_MESH_KEY, exporter.getMeshExporter());
        } else {
            JsonDiffExporter diff = new JsonDiffExporter();
            map.putProperty(EXPORT_JSON_KEY, diff.processDiff(
                    map.getProperty(EXPORT_JSON_KEY), map.getProperty(EXPORT_MESH_KEY), map
            ));
            map.putProperty(EXPORT_MESH_KEY, diff.getLatestMesh());
        }
    }

}
