package pfe.terrain.gen.algo.biome;

public enum BiomeStyle {

    CLASSIC("0   0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9\n" +
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
            "0    BEA BEA BEA BEA BEA trF trF trF MAN MAN"),

    CARIBBEAN("0   0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9\n" +
            "0.75 SHR SHR trS trS trS trS trS trS trF trF\n" +
            "0.45 SHR SHR SHR SHR trS trS trS trF trF trF\n" +
            "0.25 SHR SHR SHR trS trS trF trF trF trF trF\n" +
            "0    SHR SHR BEA BEA BEA BEA MAN MAN MAN MAN"),

    NORDIC("0   0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9\n" +
            "0.85  ALP ALP ALP ALP ALP SNO SNO SNO SNO SNO\n" +
            "0.70  ALP ALP TUN TUN TUN TUN TAI TAI TAI TAI\n" +
            "0.50  TUN TUN TUN TUN teF teF teF teF TAI TAI\n" +
            "0.25  GRA TUN TUN teF teF teF teF teF teF TAI\n" +
            "0.10  GRA GRA teF teF teF teF teF teF teF teF\n" +
            "0     GRA GRA BEA BEA BEA BEA BEA teF teF teF");

    private String whit;

    private BiomeStyle(String whit) {
        this.whit = whit;
    }

    public String getWhit() {
        return whit;
    }
}

