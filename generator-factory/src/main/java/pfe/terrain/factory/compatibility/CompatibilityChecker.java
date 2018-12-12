package pfe.terrain.factory.compatibility;

import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.exception.CompatibilityException;
import pfe.terrain.factory.storage.CompatibilityStorage;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.List;

public class CompatibilityChecker {
    private List<Algorithm> contracts;
    private CompatibilityStorage storage;


    public CompatibilityChecker(List<Algorithm> contractList){
        this.contracts = contractList;
        this.storage = new CompatibilityStorage();
    }

    public void check() throws CompatibilityException{

        for(int i = 0 ; i< contracts.size() ; i++){
            for(int j = i ; j < contracts.size() ; j++){
                if(i == j) continue;

                Algorithm a = contracts.get(i);
                Algorithm b = contracts.get(j);

                this.storage.getCompatibility(a,b).check(a,b);
            }
        }
    }
}
