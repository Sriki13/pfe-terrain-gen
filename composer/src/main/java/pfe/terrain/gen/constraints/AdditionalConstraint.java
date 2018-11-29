package pfe.terrain.gen.constraints;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.List;

public interface AdditionalConstraint {

    void apply(Model model, List<Contract> contracts, IntVar[] vars);
}
