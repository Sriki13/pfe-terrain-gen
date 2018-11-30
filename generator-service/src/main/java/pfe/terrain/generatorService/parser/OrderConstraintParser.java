package pfe.terrain.generatorService.parser;

import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.constraints.AdditionalConstraint;
import pfe.terrain.gen.constraints.ContractOrder.ContractOrder;

import java.util.List;
import java.util.Map;

public class OrderConstraintParser implements SingleConstraintParser{

    private String name;
    private String beforeKey = "before";
    private String afterKey = "after";

    public OrderConstraintParser(){
        this.name = new ContractOrder().getName();
    }

    public AdditionalConstraint getConstraint(Map<String,String> map, List<Contract> contracts){
        return new ContractOrder(map.get(this.beforeKey),map.get(this.afterKey),contracts);
    }

    public String getName(){
        return this.name;
    }
}
