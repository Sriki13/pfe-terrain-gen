package pfe.terrain.factory.entities;

import pfe.terrain.factory.pom.BasePom;
import pfe.terrain.factory.pom.Dependency;

import java.util.List;

public class Composition {
    private String name;
    private BasePom pom;
    private String context;

    public Composition(){
        this.name = "default";
        this.context = "{}";

        this.pom = new BasePom();
    }

    public Composition(String name, List<Algorithm> algorithms, String context){
        this();
        this.name = name;

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
}
