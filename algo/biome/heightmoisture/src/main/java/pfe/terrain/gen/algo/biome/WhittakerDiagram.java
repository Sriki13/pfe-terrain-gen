package pfe.terrain.gen.algo.biome;

import pfe.terrain.gen.algo.island.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class WhittakerDiagram {

    private TreeMap<Double, TreeMap<Double, Biome>> whittaker;
    private double ice;

    public WhittakerDiagram(String whittakerMatrix, double ice) {
        this.whittaker = parseWhittakerMatrix(whittakerMatrix);
        this.ice = ice;
    }

    public double getIce() {
        return ice;
    }

    public TreeMap<Double, TreeMap<Double, Biome>> getWhittaker() {
        return whittaker;
    }

    public Biome getBiome(double moisture, double height) {
        return whittaker.floorEntry(moisture).getValue().floorEntry(height).getValue();
    }

    private static TreeMap<Double, TreeMap<Double, Biome>> parseWhittakerMatrix(String whittakerMatrix) {
        TreeMap<Double, TreeMap<Double, Biome>> whittaker = new TreeMap<>();
        String lines[] = whittakerMatrix.split("\\r?\\n");
        String moistureLegend[] = lines[0].split("\\s+");
        List<Double> moistureValues = new ArrayList<>();
        moistureValues.add(null);
        for (String s : moistureLegend) {
            Double d = Double.parseDouble(s);
            moistureValues.add(d);
            whittaker.put(d, new TreeMap<>());
        }
        for (int i = 1; i < lines.length; i++) {
            String heightLegend[] = lines[i].split("\\s+");
            double heightValue = Double.parseDouble(heightLegend[0]);
            for (int j = 1; j < heightLegend.length; j++) {
                whittaker.get(moistureValues.get(j)).put(heightValue, Biome.findByCode(heightLegend[j]));
            }
        }
        return whittaker;
    }
}
