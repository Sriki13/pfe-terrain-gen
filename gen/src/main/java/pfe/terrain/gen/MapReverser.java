package pfe.terrain.gen;

import pfe.terrain.gen.algo.Mappable;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.Prefix;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.TerrainMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapReverser {

    private TerrainMap terrainMap;
    private List<Contract> contracts;

    public MapReverser(TerrainMap terrainMap, List<Contract> contracts) {
        this.terrainMap = terrainMap;
        this.contracts = contracts;
    }

    public void reverseContracts() {
        getKeysToRemove().forEach(this::removeKey);
    }

    private Set<Key> getKeysToRemove() {
        Set<Key> result = new HashSet<>();
        for (Contract contract : contracts) {
            result.addAll(contract.getContract().getCreated());
        }
        return result;
    }

    private void removeKey(Key key) {
        Key<?> location = null;
        for (Prefix prefix : Prefix.values()) {
            if (key.getId().startsWith(prefix.getPrefix())) {
                location = prefix.getKey();
            }
        }
        if (location == null) {
            terrainMap.getProperties().remove(key);
        } else {
            if (terrainMap.hasProperty(location)) {
                //noinspection unchecked
                for (Mappable mappable : (Collection<Mappable>) terrainMap.getProperty(location)) {
                    mappable.getProperties().remove(key);
                }
            }
        }
    }
}
