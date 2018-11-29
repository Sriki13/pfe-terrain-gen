package pfe.terrain.gen.constraints;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.List;

public class ContractOrder implements AdditionalConstraint {

    private Contract before;
    private Contract after;

    public ContractOrder(Contract before, Contract after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public void apply(Model model, List<Contract> contracts, IntVar[] vars) {
        int before = contracts.indexOf(this.before);
        int after = contracts.indexOf(this.after);
        model.arithm(vars[before], "<", vars[after]).post();

    }
}
