package pfe.terrain.gen.water;

public enum Noise {

    RIDGED("RIDGED"),
    BILLOW("BILLOW"),
    PERLIN("PERLIN");

    private String noiseName;

    Noise(String noiseName) {
        this.noiseName = noiseName;
    }

    public String getNoiseName() {
        return noiseName;
    }
}
