package pfe.terrain.factory.entities;

import pfe.terrain.factory.pom.Dependency;
import pfe.terrain.gen.algo.constraints.Contract;

public class Algorithm {

    private String name;
    private Contract contract;

    public Algorithm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Dependency toDependency(){
        return new Dependency(this.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        if(obj instanceof Algorithm){
            return this.name.equals(((Algorithm) obj).name);
        }
        return false;
    }


}
