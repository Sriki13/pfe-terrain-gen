package pfe.terrain.gen.algo.export;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.types.SerializableType;

import java.util.Map;
import java.util.logging.Logger;

public class JsonExporter extends Contract {

    public static final Key<Void> ALL_KEY = new Key<>("All", Void.class);
    public static final Key<String> EXPORT_JSON_KEY = new Key<>("JSON", String.class);

    private Key<Integer> seedKey = new Key<>("SEED", Integer.class);

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private MeshExporter meshExporter;
    private JsonObject lastProduction;

    public MeshExporter getMeshExporter() {
        return meshExporter;
    }

    public JsonObject getLastProduction() {
        return lastProduction;
    }


    private void printTime(long start, long end, String desc) {
        logger.info(desc + " was done in " + (end - start) / 1000 + " microseconds");
    }

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(ALL_KEY), asKeySet(EXPORT_JSON_KEY));
    }

    @Override
    public String getDescription() {
        return "A Json exporter for island maps.";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        long start;
        long end;
        String delimiter = "-------------------------";
        JsonObject result = new JsonObject();
        logger.info(delimiter + " Building JSON " + delimiter);

        logger.info("Exporting mesh...");
        start = System.nanoTime();
        meshExporter = new MeshExporter(map);
        result.add("mesh", meshExporter.export());
        end = System.nanoTime();
        printTime(start, end, "Mesh exporter");

        logger.info("Exporting vertex props...");
        start = System.nanoTime();
        PropertyExporter vertexExporter = new PropertyExporter<>(meshExporter.getVerticesMap());
        result.add("vertex_props", vertexExporter.getPropsArray());
        end = System.nanoTime();
        printTime(start, end, "Vertex props export");

        logger.info("Exporting edge props...");
        start = System.nanoTime();
        PropertyExporter edgeExporter = new PropertyExporter<>(meshExporter.getEdgesMap());
        result.add("edge_props", edgeExporter.getPropsArray());
        end = System.nanoTime();
        printTime(start, end, "Edge props export");

        logger.info("Exporting face props...");
        start = System.nanoTime();
        PropertyExporter faceExporter = new PropertyExporter<>(meshExporter.getFacesMap());
        result.add("face_props", faceExporter.getPropsArray());
        end = System.nanoTime();
        printTime(start, end, "Face props export");

        logger.info("Exporting map props...");
        start = System.nanoTime();
        Map<Key<?>, Object> properties = map.getProperties();
        for (Key key : properties.keySet()) {
            if (key.isSerialized() && properties.get(key) instanceof SerializableType) {
                JsonElement serialized = ((SerializableType) properties.get(key)).serialize();
                if (serialized != null) {
                    result.add(key.getSerializedName(), serialized);
                }
            }
        }
        end = System.nanoTime();
        printTime(start, end, "Map props export");

        result.addProperty("uuid", map.getProperty(seedKey));
        logger.info(delimiter + " Done building JSON " + delimiter);
        lastProduction = result;
        map.putProperty(EXPORT_JSON_KEY, result.toString());
    }

}
