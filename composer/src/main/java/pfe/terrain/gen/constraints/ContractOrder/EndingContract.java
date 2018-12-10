package pfe.terrain.gen.constraints.ContractOrder;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import pfe.terrain.gen.DependencySolver;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.constraints.AdditionalConstraint;
import pfe.terrain.gen.exception.MultipleEnderException;
import pfe.terrain.gen.exception.UnsolvableException;

import java.util.List;

public class EndingContract implements AdditionalConstraint {
    private Contract ending;

    public EndingContract(List<Contract> contracts) throws MultipleEnderException{
        for(Contract contract : contracts){
            if(contract.getContract().getRequired().contains(DependencySolver.allKey)){
                if(ending != null){
                    throw new MultipleEnderException(contract,this.ending);
                } else {
                    this.ending = contract;
                }
            }
        }
    }


    @Override
    public void apply(Model model, List<Contract> contracts, IntVar[] vars) {
        if(ending == null) return;

        int end = contracts.indexOf(ending);

        for(Contract contract : contracts){
            if(!contract.equals(this.ending)){
                model.arithm(vars[contracts.indexOf(contract)], "<", vars[end]).post();
            }
        }
    }

    @Override
    public String getName() {
        return "End";
    }
}
