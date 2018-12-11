package pfe.terrain.generatorService.holder;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Algorithm algorithm = (Algorithm) o;
        return pos == algorithm.pos &&
                Objects.equals(name, algorithm.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pos);
    }
}
