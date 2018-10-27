package pfe.terrain.gen.algo;

public enum Property {
    POINTS("Points"), VERTICES("Vertices"), EDGES("Edges"), FACES("Faces");

    private final String name;

    Property(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
