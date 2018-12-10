package pfe.terrain.gen.parser;

import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.constraints.AdditionalConstraint;
import pfe.terrain.gen.constraints.ContractOrder.ContractOrder;
import pfe.terrain.gen.exception.NoSuchContractException;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class OrderConstraintParser implements SingleConstraintParser{
    Logger logger = Logger.getLogger("ConstraintParser");

    private String name;
    private String beforeKey = "before";
    private String afterKey = "after";

    public OrderConstraintParser(){
        this.name = new ContractOrder().getName();
    }

    public AdditionalConstraint getConstraint(Map<String,String> map, List<Contract> contracts) throws NoSuchContractException {
        return new ContractOrder(map.get(this.beforeKey), map.get(this.afterKey), contracts);

    }

    public String getName(){
        return this.name;
    }
}
