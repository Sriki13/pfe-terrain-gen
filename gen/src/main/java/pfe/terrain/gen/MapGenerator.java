package pfe.terrain.gen;

import pfe.terrain.gen.algo.Generator;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.export.JsonExporter;
import pfe.terrain.gen.export.diff.JsonDiffExporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapGenerator implements Generator {

    private List<Contract> contracts;
    private TerrainMap terrainMap;
    private List<Contract> diffContracts;
    private Context context;
    private JsonExporter exporter;

    public MapGenerator(List<Contract> contracts) {
        this.contracts = contracts;
        this.terrainMap = new TerrainMap();
        this.context = new Context();
        this.diffContracts = new ArrayList<>();
    }

    @Override
    public String generate(boolean diffOnly) {
        boolean errored = false;
        long start = System.nanoTime();
        StringBuilder sb = new StringBuilder("\n\n");
        String separator = "---------------------------------------------------------------------\n";
        sb.append(separator);
        sb.append("Map Generation Summary\n");
        sb.append(separator);
        sb.append('\n');
        RuntimeException rte = null;
        for (Contract ctr : contracts) {
            try {
                if (errored) {
                    sb.append(formatExecution(ctr.getName(), "SKIPPED", 0));
                } else {
                    long execTime = ctr.debugExecute(this.terrainMap, this.context);
                    sb.append(formatExecution(ctr.getName(), "SUCCESS", execTime));
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
                errored = true;
                sb.append(formatExecution(ctr.getName(), "FAILURE", 0));
                rte = e;
            }
        }
        String result = "";
        JsonExporter lastExporter = new JsonExporter();
        if (errored) {
            sb.append(formatExecution("JSONExportation", "SKIPPED", 0));
        } else {
            try {
                long startTime = System.nanoTime();
                if (diffOnly && exporter != null) {
                    JsonDiffExporter diff = new JsonDiffExporter(exporter, lastExporter);
                    lastExporter.export(terrainMap);
                    result = diff.getDiff(this.terrainMap).toString();
                } else {
                    result = lastExporter.export(this.terrainMap).toString();
                }
                exporter = lastExporter;
                long endTime = System.nanoTime();
                sb.append(formatExecution("JSONExportation", "SUCCESS", endTime - startTime));
            } catch (RuntimeException e) {
                e.printStackTrace();
                Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
                errored = true;
                sb.append(formatExecution("JSONExportation", "FAILURE", 0));
                rte = e;
            }
        }
        sb.append('\n');
        sb.append(separator);
        if (errored) {
            sb.append(formatExecution("MapGeneration", "FAILURE", System.nanoTime() - start));
        } else {
            sb.append(formatExecution("MapGeneration", "SUCCESS", System.nanoTime() - start));
        }
        sb.append(separator);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, sb.toString());
        if (rte != null) {
            throw rte;
        }
        return result;
    }


    @Override
    public void setParams(Context map) {
        this.context = map;
    }

    @Override
    public List<Contract> getContracts() {
        return this.contracts;
    }

    private String formatExecution(String contractName, String execCode, long execTime) {
        StringBuilder sb = new StringBuilder();
        sb.append(contractName);
        char[] chars = new char[50 - contractName.length()];
        Arrays.fill(chars, '.');
        sb.append(new String(chars));
        sb.append(String.format("%1$8s", execCode));
        sb.append(" [");
        sb.append(String.format("%1$6.3f", execTime / 1000000000.0));
        sb.append(" s]\n");
        return sb.toString();
    }


}
