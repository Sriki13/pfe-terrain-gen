package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;

import java.util.*;

public class DiffSolver {

    private List<Param<?>> modifiedParams;

    public DiffSolver(Context original, Context latest) {
        this.modifiedParams = new ArrayList<>();
        Map<Param<?>, Object> originalProps = original.getProperties();
        Map<Param<?>, Object> newestProps = latest.getProperties();
        for (Param<?> param : newestProps.keySet()) {
            if (!originalProps.containsKey(param) || !originalProps.get(param).equals(newestProps.get(param))) {
                modifiedParams.add(param);
            }
        }
    }

    public List<Contract> getContractsToExecute(List<Contract> contracts) {
        int min = contracts.size();

        for (Contract contract : contracts) {
            Set<Param> requested = contract.getRequestedParameters();
            if (requested == null) {
                continue;
            }
            for (Param<?> param : modifiedParams) {
                if (requested.contains(param)) {
                    if (contracts.indexOf(contract) < min) {
                        min = contracts.indexOf(contract);
                    }
                }
            }
        }
        while (true) {
            Set<Key> modifiedKeys = new HashSet<>();
            for (int i = min; i < contracts.size(); i++) {
                modifiedKeys.addAll(contracts.get(i).getContract().getModified());
            }
            int newMin = findMinContract(modifiedKeys, contracts, min);
            if (min == newMin) {
                break;
            } else {
                min = newMin;
            }
        }

        return contracts.subList(min, contracts.size());
    }

    private int findMinContract(Set<Key> modifiedKeys, List<Contract> contracts, int min) {
        for (Key key : modifiedKeys) {
            for (int i = 0; i < contracts.size(); i++) {
                Contract current = contracts.get(i);
                if (current.getContract().getCreated().contains(key)) {
                    if (contracts.indexOf(current) < min) {
                        return contracts.indexOf(current);
                    }
                    break;
                }
            }
        }
        return min;
    }


}
