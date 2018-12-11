package pfe.terrain.gen.cave;

public enum Noise {

    RIDGED("RIDGED"),
    PERLIN("PERLIN");

    private String noiseName;

    Noise(String noiseName) {
        this.noiseName = noiseName;
    }

    public String getNoiseName() {
        return noiseName;
    }
}
