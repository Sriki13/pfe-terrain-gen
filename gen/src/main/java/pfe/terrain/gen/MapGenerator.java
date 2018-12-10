package pfe.terrain.gen;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;
import pfe.terrain.gen.algo.Generator;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.export.JsonExporter;
import pfe.terrain.gen.export.diff.JsonDiffExporter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapGenerator implements Generator {

    private List<Contract> original;
    private List<Contract> contracts;
    private TerrainMap terrainMap;
    private Context context;
    private JsonExporter exporter;

    private boolean hasRunOnce = false;
    private Map<String, Long> mapMillis;

    public MapGenerator(List<Contract> contracts) {
        this.original = contracts;
        this.contracts = contracts;
        this.terrainMap = new TerrainMap();
        this.context = new Context();
    }

    @Override
    public String generate(boolean diffOnly) {
        mapMillis = new HashMap<>();
        hasRunOnce = true;
        boolean errored = false;
        long start = System.nanoTime();
        StringBuilder sb = new StringBuilder("\n\n");
        String separator = "---------------------------------------------------------------------\n";
        sb.append(separator);
        sb.append("Map Generation Summary\n");
        sb.append(separator);
        sb.append('\n');
        RuntimeException rte = null;
        if (original.size() > contracts.size()) {
            for (int i = 0; i < original.size() - contracts.size(); i++) {
                sb.append(formatExecution(original.get(i).getName(), "SKIPPED", 0));
            }
        }
        for (Contract ctr : contracts) {
            try {
                if (errored) {
                    sb.append(formatExecution(ctr.getName(), "SKIPPED", 0));
                } else {
                    long execTime = ctr.debugExecute(this.terrainMap, this.context);
                    mapMillis.put(ctr.getName(), execTime);
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
                long execTime = endTime - startTime;
                sb.append(formatExecution("JSONExportation", "SUCCESS", execTime));
                mapMillis.put("JSONExport", execTime);
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
        long totalTime = System.nanoTime() - start;
        if (errored) {
            sb.append(formatExecution("MapGeneration", "FAILURE", totalTime));
        } else {
            sb.append(formatExecution("MapGeneration", "SUCCESS", totalTime));
        }
        sb.append(separator);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, sb.toString());
        if (rte != null) {
            throw rte;
        }
        return result;
    }

    public byte[] getExecutionChart() {
        if (hasRunOnce) {
            AtomicLong totalTime = new AtomicLong();
            mapMillis.forEach((k, v) -> totalTime.addAndGet(v));
            PieChart chart = new PieChartBuilder().width(800).height(600).title("Exec Times (total: " + totalTime.get() / 1000000 + "ms)").theme(Styler.ChartTheme.GGPlot2).build();

            chart.getStyler().setLegendVisible(false);
            chart.getStyler().setAnnotationType(PieStyler.AnnotationType.LabelAndPercentage);
            chart.getStyler().setAnnotationDistance(1.7);
            chart.getStyler().setPlotContentSize(.5);
            chart.getStyler().setDrawAllAnnotations(true);
            chart.addSeries("Export", mapMillis.get("JSONExport"));
            for (int i = contracts.size() - 1; i >= 0; i--) {
                chart.addSeries(contracts.get(i).getName(), mapMillis.get(contracts.get(i).getName()));
            }
            List<String> toRemove = new ArrayList<>();
            chart.getSeriesMap().forEach((k, v) -> {
                if (v.getValue().longValue() < totalTime.get() / 100) {
                    toRemove.add(k);
                }
            });
            toRemove.forEach(chart::removeSeries);
            try {
                return BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error with bitmap serialization");
            }
        } else {
            throw new RuntimeException("No executions to base stats upon");
        }
    }


    @Override
    public void setParams(Context map) {
        if (!hasRunOnce) {
            this.context = map;
            return;
        }
        DiffSolver solver = new DiffSolver(this.context, map);
        this.contracts = solver.getContractsToExecute(this.original);
        MapReverser reverser = new MapReverser(this.terrainMap, this.contracts);
        reverser.reverseContracts();
        this.context = map;
    }

    @Override
    public List<Contract> getContracts() {
        return this.original;
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
