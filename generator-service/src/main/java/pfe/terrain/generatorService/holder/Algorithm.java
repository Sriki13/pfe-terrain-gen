package pfe.terrain.generatorService.holder;

public class Algorithm {

    private String name;
    private int pos;

    public Algorithm(String name, int pos) {
        this.name = name;
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    public int getPos() {
        return pos;
    }
}
