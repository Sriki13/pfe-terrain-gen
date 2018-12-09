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
        Set<Key> modifiedKeys = new HashSet<>();
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
                    modifiedKeys.addAll(contract.getContract().getModified());
                }
            }
        }
        for (Key key : modifiedKeys) {
            for (int i = 0; i < contracts.size(); i++) {
                Contract current = contracts.get(i);
                if (current.getContract().getCreated().contains(key)) {
                    if (contracts.indexOf(current) < min) {
                        min = contracts.indexOf(current);
                    }
                }
            }
        }
        return contracts.subList(min, contracts.size());
    }


}
