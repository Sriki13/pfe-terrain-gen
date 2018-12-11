package pfe.terrain.gen;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;
import pfe.terrain.gen.algo.Generator;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.TerrainMap;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapGenerator implements Generator {

    private static final String SEPARATOR = "---------------------------------------------------------------------\n";

    private List<Contract> original;
    private List<Contract> contracts;
    private TerrainMap terrainMap;
    private Context context;
    private Context lastExecContext;

    private boolean hasRunOnce = false;
    private Map<String, Long> mapMillis;

    public MapGenerator(List<Contract> contracts) {
        this.original = contracts;
        this.contracts = contracts;
        this.terrainMap = new TerrainMap();
        this.context = new Context();
    }

    @Override
    public void generate() {
        mapMillis = new HashMap<>();
        hasRunOnce = true;
        long start = System.nanoTime();
        solveDiff();
        StringBuilder sb = initLogs();
        RuntimeException rte = executeContracts(sb);
        finishLogs(rte, sb, start);
        if (rte != null) {
            throw rte;
        }
        this.lastExecContext = context;
    }

    @Override
    public Object getProperty(String keyId) {
        for (Map.Entry<Key<?>, Object> entry : terrainMap.getProperties().entrySet()) {
            if (entry.getKey().getId().equals(keyId)) {
                return entry.getValue();
            }
        }
        throw new NoSuchKeyException(keyId);
    }

    public void solveDiff() {
        if (this.lastExecContext != null) {
            DiffSolver solver = new DiffSolver(this.lastExecContext, context);
            this.contracts = solver.getContractsToExecute(this.original);
            MapReverser reverser = new MapReverser(this.terrainMap, this.contracts);
            reverser.reverseContracts();
        }
    }

    private StringBuilder initLogs() {
        StringBuilder sb = new StringBuilder("\n\n");
        sb.append(SEPARATOR);
        sb.append("Map Generation Summary\n");
        sb.append(SEPARATOR);
        sb.append('\n');
        if (original.size() > contracts.size()) {
            for (int i = 0; i < original.size() - contracts.size(); i++) {
                sb.append(formatExecution(original.get(i).getName(), "SKIPPED", 0));
            }
        }
        return sb;
    }

    private RuntimeException executeContracts(StringBuilder sb) {
        boolean errored = false;
        RuntimeException rte = null;
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
        return rte;
    }

    private void finishLogs(RuntimeException rte, StringBuilder sb, long start) {
        sb.append('\n');
        sb.append(SEPARATOR);
        long totalTime = System.nanoTime() - start;
        if (rte != null) {
            sb.append(formatExecution("MapGeneration", "FAILURE", totalTime));
            sb.append(SEPARATOR);
        } else {
            sb.append(formatExecution("MapGeneration", "SUCCESS", totalTime));
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, sb.toString());
    }

    public byte[] getExecutionChart() {
        if (hasRunOnce) {
            AtomicLong totalTime = new AtomicLong();
            mapMillis.forEach((k, v) -> totalTime.addAndGet(v));
            PieChart chart = new PieChartBuilder().width(800).height(600)
                    .title("Exec Times (total: " + totalTime.get() / 1000000 + "ms)")
                    .theme(Styler.ChartTheme.GGPlot2)
                    .build();

            chart.getStyler().setLegendVisible(false);
            chart.getStyler().setAnnotationType(PieStyler.AnnotationType.LabelAndPercentage);
            chart.getStyler().setAnnotationDistance(1.7);
            chart.getStyler().setPlotContentSize(.5);
            chart.getStyler().setDrawAllAnnotations(true);
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
