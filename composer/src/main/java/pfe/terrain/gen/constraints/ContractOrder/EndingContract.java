package pfe.terrain.gen.constraints.ContractOrder;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import pfe.terrain.gen.DependencySolver;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.constraints.AdditionalConstraint;
import pfe.terrain.gen.exception.MultipleEnderException;

import java.util.ArrayList;
import java.util.List;

public class EndingContract implements AdditionalConstraint {
    private List<Contract> ending;

    public EndingContract(List<Contract> contracts){
        this.ending = new ArrayList<>();
        for(Contract contract : contracts){
            if (contract.getContract().getRequired().contains(DependencySolver.ALL_KEY)) {
                ending.add(contract);
            }
        }
    }


    @Override
    public void apply(Model model, List<Contract> contracts, IntVar[] vars) {
        for(Contract ender : ending) {
            int end = contracts.indexOf(ender);

            for (Contract contract : contracts) {
                if (!this.ending.contains(contract)) {
                    model.arithm(vars[contracts.indexOf(contract)], "<", vars[end]).post();
                }
            }
        }
    }

    @Override
    public String getName() {
        return "End";
    }
}
