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
            if (!originalProps.containsKey(param) || originalProps.get(param) != newestProps.get(param)) {
                modifiedParams.add(param);
            }
        }
    }

    public List<Contract> getContractsToExecute(List<Contract> contracts) {
        List<Contract> impacted = new ArrayList<>();
        for (Contract contract : contracts) {
            Set<Param> requested = contract.getRequestedParameters();
            if (requested == null) {
                continue;
            }
            for (Param<?> param : modifiedParams) {
                if (requested.contains(param)) {
                    impacted.add(contract);
                }
            }
        }
        if (impacted.isEmpty()) {
            return impacted;
        }
        Set<Key> impactedKeys = new HashSet<>();
        for (int i = contracts.indexOf(impacted.get(0)); i < contracts.size(); i++) {
            Contract current = contracts.get(i);
            if (impacted.contains(current)) {
                impactedKeys.addAll(current.getContract().getCreated());
                impactedKeys.addAll(current.getContract().getModified());
            } else {
                for (Key key : impactedKeys) {
                    if (current.getContract().getRequired().contains(key) ||
                            current.getContract().getModified().contains(key)) {
                        impacted.add(current);
                        impactedKeys.addAll(current.getContract().getCreated());
                        impactedKeys.addAll(current.getContract().getModified());
                    }
                }
            }
        }
        return impacted;
    }


}
