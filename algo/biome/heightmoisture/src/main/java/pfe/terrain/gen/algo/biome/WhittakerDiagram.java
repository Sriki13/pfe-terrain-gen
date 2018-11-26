package pfe.terrain.gen.algo.biome;

import pfe.terrain.gen.algo.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class WhittakerDiagram {

    public static String WCLASSIC =
            "0   0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9\n" +
                    "0.98 ALP ALP ALP ALP ALP SNO SNO SNO SNO SNO\n" +
                    "0.93 ALP ALP TUN TUN TUN TUN TAI TAI SNO SNO\n" +
                    "0.89 ALP TUN TUN TUN TUN TUN TAI TAI TAI SNO\n" +
                    "0.79 teD TUN TUN SHR SHR SHR TAI TAI TAI TAI\n" +
                    "0.69 teD teD teD SHR SHR SHR teF TAI TAI TAI\n" +
                    "0.6  teD teD SHR SHR SHR SHR teF teF TAI TAI\n" +
                    "0.51 teD teD GRA GRA teF teF teF teF teF teF\n" +
                    "0.43 teD teD GRA GRA GRA GRA teF teF teF teR\n" +
                    "0.36 teD GRA GRA GRA teF teF teF teF teR teR\n" +
                    "0.3  teD GRA GRA GRA trS trS teR teR teR teR\n" +
                    "0.21 STD GRA GRA GRA trS trS teF trF trF trF\n" +
                    "0.16 STD GRA trS trS trS trS trF trF trF trF\n" +
                    "0.08 STD STD STD trS trS trF trF trF trF MAN\n" +
                    "0    BEA BEA BEA BEA BEA trF trF trF MAN MAN";


    static String WCARIBBEAN =
            "0   0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9\n" +
                    "0.8 SHR SHR trS trS trS trS trS trS trF trF\n" +
                    "0.65 SHR SHR SHR SHR trS trS trS trF trF trF\n" +
                    "0.45 SHR SHR SHR trS trS trF trF trF trF trF\n" +
                    "0.15 SHR SHR BEA BEA BEA BEA MAN MAN MAN MAN";

    static String WNORDIC =
            "0   0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9\n" +
                    "0.89  ALP ALP ALP ALP ALP SNO SNO SNO SNO SNO\n" +
                    "0.76  ALP ALP TUN TUN TUN TUN TAI TAI TAI TAI\n" +
                    "0.55  TUN TUN TUN TUN teF teF teF teF TAI TAI\n" +
                    "0.30  GRA TUN TUN teF teF teF teF teF teF TAI\n" +
                    "0.15  GRA GRA teF teF teF teF teF teF teF teF\n" +
                    "0.05  GRA GRA BEA BEA BEA BEA BEA teF teF teF";


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
