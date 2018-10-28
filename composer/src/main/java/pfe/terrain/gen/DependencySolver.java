package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Contract;

import java.util.ArrayList;
import java.util.List;

public class DependencySolver {

    private List<Dependency> priority;
    private List<Dependency> available;
    private Contract finalMap;

    private List<Contract> ordered;

    public DependencySolver(List<Contract> available, List<Contract> priority, Contract finalMap)
            throws InvalidContractException {
        this.available = buildDeps(available);
        this.priority = buildDeps(priority);
        this.ordered = new ArrayList<>();
        this.finalMap = finalMap;
    }

    public List<Contract> orderContracts() throws InvalidContractException, UnsolvableException {
        solve(new Dependency(finalMap));
        if (!priority.isEmpty()) {
            System.out.println("WARNING: The final contract passed to the map did \n" +
                    "not have all the desired elements as its requirements. As such,\n" +
                    "the following contract elements might be missing from the map generator.");
            for (Dependency dependency : priority) {
                System.out.println(dependency.getContract().toString());
            }
        }
        return ordered;
    }

    private void solve(Dependency dependency) throws UnsolvableException {
        while (!dependency.isSolved()) {
            Dependency next = findParent(dependency, priority);
            if (next == null) {
                next = findParent(dependency, available);
                if (next == null) {
                    throw new UnsolvableException();
                }
            } else {
                priority.remove(next);
            }
            if (!next.isSolved()) {
                solve(next);
            }
            ordered.add(next.getContract());
            dependency.notifySolved(next);
            notifyAllSolved(next);
        }
    }

    private void notifyAllSolved(Dependency solved) {
        for (Dependency dependency : priority) {
            dependency.notifySolved(solved);
        }
        for (Dependency dependency : available) {
            dependency.notifySolved(solved);
        }
    }

    private Dependency findParent(Dependency child, List<Dependency> possibleParents) {
        for (Dependency parent : possibleParents) {
            if (parent.partiallySolves(child)) {
                return parent;
            }
        }
        return null;
    }

    private List<Dependency> buildDeps(List<Contract> contracts) throws InvalidContractException {
        List<Dependency> result = new ArrayList<>();
        for (Contract contract : contracts) {
            result.add(new Dependency(contract));
        }
        return result;
    }

}
