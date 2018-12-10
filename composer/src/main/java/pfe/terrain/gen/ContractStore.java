package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.exception.MissingRequiredException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContractStore {
    List<Contract> contracts;

    public ContractStore(List<Contract> contracts) {
        this.contracts = new ArrayList<>(contracts);
    }

    public List<Contract> getContracts() {
        return new ArrayList<>(contracts);
    }

    /**
     * @return all the required element by the stored contract
     */
    public Set<Key> getAllRequired() {
        Set<Key> required = new HashSet<>();

        for (Contract dependency : contracts) {
            required.addAll(dependency.getContract().getRequired());
            required.addAll(dependency.getContract().getModified());
        }

        required.remove(DependencySolver.allKey);

        return required;
    }

    /**
     * @return all the created element by the stored contract
     */
    public Set<Key> getAllCreated() {
        Set<Key> created = new HashSet<>();

        for (Contract dependency : contracts) {
            created.addAll(dependency.getContract().getCreated());
        }

        return created;
    }

    /**
     * add the conract to the list
     *
     * @param contract to be add
     */
    public void add(Contract contract) {
        contracts.add(contract);
    }

    /**
     * look for the contract providing the given element
     *
     * @param creation element to find in the stored contract
     * @return the contract providing the desired element
     * @throws MissingRequiredException if the element is not provided by the stored contract
     */
    public Contract getContractCreating(Key creation) throws MissingRequiredException {
        for (Contract contract : contracts) {
            if (contract.getContract().getCreated().contains(creation)) {
                return contract;
            }
        }
        throw new MissingRequiredException(creation.getId());
    }
}
