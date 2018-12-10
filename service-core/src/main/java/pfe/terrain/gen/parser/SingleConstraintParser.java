package pfe.terrain.gen.parser;

import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.constraints.AdditionalConstraint;

import java.util.List;
import java.util.Map;

public interface SingleConstraintParser {

    String getName();

    AdditionalConstraint getConstraint(Map<String,String> map, List<Contract> contracts) throws Exception;
}
