package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.HashSet;
import java.util.Set;

public class Dependency {

    private final Contract contract;
    private final Set<String> created;

    private Set<String> required;

    public Dependency(Contract contract) throws InvalidContractException {
        this.contract = contract;
        this.required = new HashSet<>();
        Constraints constraints = contract.getContract();
        this.created = constraints.getCreated();
        for (String prop : constraints.getCreated()) {
            if (constraints.getRequired().contains(prop)) {
                throw new InvalidContractException(contract);
            }
        }
        required.addAll(constraints.getRequired());
    }

    public boolean isSolved() {
        return required.isEmpty();
    }

    public boolean partiallySolves(Dependency dependency) {
        for (String property : dependency.getRequired()) {
            if (created.contains(property)) {
                return true;
            }
        }
        return false;
    }

    public void notifySolved(Dependency dependency) {
        required.removeAll(dependency.getCreated());
    }

    public Contract getContract() {
        return contract;
    }

    public Set<String> getRequired() {
        return required;
    }

    public Set<String> getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "contract=" + contract +
                ", created=" + created +
                ", required=" + required +
                '}';
    }
}
