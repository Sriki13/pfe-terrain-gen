package pfe.terrain.factory.entities;

import pfe.terrain.factory.pom.BasePom;
import pfe.terrain.factory.pom.Dependency;

import java.util.List;
import java.util.Objects;

public class Composition {
    private String name;
    private BasePom pom;
    private String context;
    private List<Algorithm> algorithms;

    public Composition(){
        this.name = "default";
        this.context = "{}";

        this.pom = new BasePom();
    }

    public Composition(String name, List<Algorithm> algorithms, String context){
        this();
        this.name = name;
        this.algorithms = algorithms;
        for(Algorithm algo : algorithms){
            this.pom.addDependency(new Dependency(algo.getName()));
        }

        this.context = context;
    }

    public String getName() {
        return name;
    }

    public BasePom getPom() {
        return pom;
    }

    public String getContext() {
        return context;
    }

    public List<Algorithm> getAlgorithms() {
        return algorithms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Composition that = (Composition) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
