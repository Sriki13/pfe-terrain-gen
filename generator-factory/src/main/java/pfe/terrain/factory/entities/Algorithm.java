package pfe.terrain.factory.entities;

import pfe.terrain.factory.pom.Dependency;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.NotExecutableContract;

import java.util.HashSet;

public class Algorithm {

    private String id;
    private Contract contract;

    public Algorithm(String id) {
        this.id = id;
        this.contract = new NotExecutableContract(id,"auto-generated contract",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>()));
    }

    public Algorithm(Contract contract, String id) {
        this.id = id;
        this.contract = contract;
    }

    public String getId(){
        return this.id;
    }

    public String getName() {
        return this.contract.getName();
    }

    public Dependency toDependency(){
        return new Dependency(this.getId());
    }

    public Contract getContract(){
        return this.contract;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        if(obj instanceof Algorithm){
            return this.getName().equals(((Algorithm) obj).getName());
        }
        return false;
    }


}
