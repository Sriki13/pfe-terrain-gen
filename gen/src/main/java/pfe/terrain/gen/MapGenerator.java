package pfe.terrain.gen;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.export.JSONExporter;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapGenerator implements Generator {

    private List<Contract> contracts;
    private IslandMap islandMap;
    private Context context;

    public MapGenerator(List<Contract> contracts) {
        this.contracts = contracts;
        this.islandMap = new IslandMap();
        this.context = new Context();
    }


    public String generate() {

        boolean errored = false;
        long start = System.nanoTime();
        StringBuilder sb = new StringBuilder("\n\n");
        String separator = "---------------------------------------------------------------------\n";
        sb.append(separator);
        sb.append("Map Generation Summary\n");
        sb.append(separator);
        sb.append('\n');
        for (Contract ctr : contracts) {
            try {
                if (errored) {
                    sb.append(formatExecution(ctr.getName(), "SKIPPED", 0));
                } else {
                    long execTime = ctr.debugExecute(this.islandMap, this.context);
                    sb.append(formatExecution(ctr.getName(), "SUCCESS", execTime));
                }
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
                errored = true;
                sb.append(formatExecution(ctr.getName(), "FAILURE", 0));
            }
        }
        String result = "";
        if (errored) {
            sb.append(formatExecution("JSONExportation", "SKIPPED", 0));
        } else {
            JSONExporter exporter = new JSONExporter();
            try {
                long startTime = System.nanoTime();
                result = exporter.export(this.islandMap).toString();
                long endTime = System.nanoTime();
                sb.append(formatExecution("JSONExportation", "SUCCESS", endTime - startTime));
            } catch (Exception e) {
                errored = true;
                Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
                sb.append(formatExecution("JSONExportation", "FAILURE", 0));
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
