package pfe.terrain.gen.cave;

public enum Ore {

    COPPER("Cu", 0.03, 0.2),
    IRON("Fe", 0.02, 0.15),
    GOLD("Au", 0.01, 0.08),
    DIAMOND("Di", 0.002, 0.03);

    private String id;
    private double rarity;
    private double spreading;

    Ore(String id, double rarity, double spreading) {
        this.id = id;
        this.rarity = rarity;
        this.spreading = spreading;
    }

    public String getId() {
        return id;
    }

    public double getRarity() {
        return rarity;
    }

    public double getSpreading() {
        return spreading;
    }
}
