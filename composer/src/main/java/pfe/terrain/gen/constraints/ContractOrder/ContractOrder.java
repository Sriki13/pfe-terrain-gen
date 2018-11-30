package pfe.terrain.gen.constraints.ContractOrder;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.constraints.AdditionalConstraint;
import pfe.terrain.gen.exception.NoSuchContractException;

import java.util.List;

public class ContractOrder implements AdditionalConstraint {

    private Contract before;
    private Contract after;

    public ContractOrder(){

    }

    public ContractOrder(Contract before, Contract after) {
        this.before = before;
        this.after = after;
    }

    public ContractOrder(String beforeName, String afterName, List<Contract> contracts) throws NoSuchContractException{
        for(Contract contract : contracts){
            if(contract.getName().equals(beforeName)){
                this.before = contract;
            }

            if(contract.getName().equals(afterName)){
                this.after = contract;
            }
        }

        if(this.after == null || this.before == null){
            throw new NoSuchContractException();
        }
    }

    @Override
    public void apply(Model model, List<Contract> contracts, IntVar[] vars) {
        int before = contracts.indexOf(this.before);
        int after = contracts.indexOf(this.after);
        model.arithm(vars[before], "<", vars[after]).post();
    }

    @Override
    public String getName() {
        return "order";
    }
}
