package pfe.terrain.gen;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import pfe.terrain.gen.algo.constraints.export.ExportDiffProcessor;

import java.util.ArrayList;
import java.util.Set;

public class ExporterReflection {

    private Class<? extends Exporter> exporterClass;
    private Class<? extends ExportDiffProcessor> diffClass;

    public ExporterReflection() {
        Reflections reflections = new Reflections("pfe.terrain.gen", new SubTypesScanner(false));
        Set<Class<? extends Exporter>> exporters = reflections.getSubTypesOf(Exporter.class);
        Set<Class<? extends ExportDiffProcessor>> diffProcessors = reflections.getSubTypesOf(ExportDiffProcessor.class);
        if (exporters.isEmpty()) {
            throw new RuntimeException("No exporter was found in the classpath");
        }
        if (exporters.size() > 1 || diffProcessors.size() > 1) {
            throw new RuntimeException("Two exporters or diff processors are present in the classpath");
        }
        exporterClass = new ArrayList<>(exporters).get(0);
        diffClass = diffProcessors.isEmpty() ? null : new ArrayList<>(diffProcessors).get(0);
    }

    public Exporter getNewExporter() {
        try {
            return exporterClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public ExportDiffProcessor getNewDiffProcessor() {
        if (diffClass == null) {
            return null;
        }
        try {
            return diffClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }


}
