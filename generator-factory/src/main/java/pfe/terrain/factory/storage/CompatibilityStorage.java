package pfe.terrain.factory.storage;

import pfe.terrain.factory.compatibility.Compatibility;
import pfe.terrain.factory.compatibility.SimpleCompatibility;
import pfe.terrain.factory.entities.Algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompatibilityStorage {

    private static Map<Algorithm, Map<Algorithm, Compatibility>> compatibilities = new HashMap<>();



    public Compatibility getCompatibility(Algorithm a, Algorithm b){
        if(compatibilities.containsKey(a) && compatibilities.get(a).containsKey(b)){
            return compatibilities.get(a).get(b);
        }  else if (compatibilities.containsKey(b) && compatibilities.get(b).containsKey(a)) {
            return compatibilities.get(b).get(a);
        } else {
            return SimpleCompatibility.UNKNOWN;
        }
    }

    public Compatibility putCompatibility(Algorithm a, Algorithm b, Compatibility simpleCompatibility){
        if(!compatibilities.containsKey(a)){
            compatibilities.put(a, new HashMap<>());
        }
        compatibilities.get(a).put(b, simpleCompatibility);

        if(!compatibilities.containsKey(b)){
            compatibilities.put(b, new HashMap<>());
        }
        compatibilities.get(b).put(a, simpleCompatibility);

        return this.getCompatibility(a,b);
    }

    public void putCompatibility(List<Algorithm> algorithms, Compatibility compatibility){
        for(Algorithm a : algorithms){
            for (Algorithm b : algorithms){
                this.putCompatibility(a,b,compatibility);
            }
        }
    }

    public void clear(){
        compatibilities = new HashMap<>();
    }
}
