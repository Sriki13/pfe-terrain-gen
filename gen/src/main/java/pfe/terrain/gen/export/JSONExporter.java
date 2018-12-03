package pfe.terrain.gen.export;

import com.google.gson.JsonObject;
import pfe.terrain.gen.algo.island.IslandMap;

import java.util.logging.Logger;

public class JSONExporter {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    public JsonObject export(IslandMap islandMap) {
        long start;
        long end;
        String delimiter = "-------------------------";
        JsonObject result = new JsonObject();
        logger.info(delimiter + " Building JSON " + delimiter);

        logger.info("Exporting mesh...");
        start = System.nanoTime();
        MeshExporter meshExporter = new MeshExporter(islandMap);
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

        result.addProperty("uuid", islandMap.getSeed());
        logger.info(delimiter + " Done building JSON " + delimiter);
        return result;
    }

    private void printTime(long start, long end, String desc) {
        logger.info(desc + " was done in " + (end - start) / 1000 + " microseconds");
    }

}
