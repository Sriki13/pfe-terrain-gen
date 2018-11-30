package pfe.terrain.generatorService.parser;

import org.chocosolver.solver.constraints.Constraint;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.constraints.AdditionalConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConstraintParser {
    private Logger log = Logger.getLogger("Constraint Parser");

    private String nameKey = "name";

    private List<SingleConstraintParser> constraints;

    public ConstraintParser(){
        constraints = new ArrayList<>();
        constraints.add(new OrderConstraintParser());
    }

    public List<AdditionalConstraint> listToConstraints(List<Map> array, List<Contract> contracts){
        List<AdditionalConstraint> constraints = new ArrayList<>();

        for(Map<String,String> map : array ){
            try{

                SingleConstraintParser parser = getParserFromName(map.get(nameKey));
                constraints.add(parser.getConstraint(map,contracts));

            }catch(Exception e){
                log.log(Level.WARNING,"could not parse constraint with name : " + map.get(nameKey));
            }
        }

        return constraints;
    }

    private SingleConstraintParser getParserFromName(String name){
        for(SingleConstraintParser parser : constraints){
            if(parser.getName().equals(name)){
                return parser;
            }
        }
        return null;
    }
}
