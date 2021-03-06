package pfe.terrain.gen;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.objective.ParetoOptimizer;
import org.chocosolver.solver.variables.IntVar;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.constraints.AdditionalConstraint;
import pfe.terrain.gen.constraints.ContractOrder.EndingContract;
import pfe.terrain.gen.exception.DuplicatedProductionException;
import pfe.terrain.gen.exception.MissingRequiredException;
import pfe.terrain.gen.exception.MultipleEnderException;
import pfe.terrain.gen.exception.UnsolvableException;

import java.util.*;

public class DependencySolver {

    private ContractStore toUse;
    private ContractStore available;

    public static final Key<Void> ALL_KEY = new Key<>("All", Void.class);

    public DependencySolver(List<Contract> priority) {
        this.toUse = new ContractStore(priority);
    }

    /**
     * check if all the needs can be satisfied and then order the list
     * @return the ordered list
     * @throws UnsolvableException thrown if the problem is not solvable by the system
     * @throws MissingRequiredException thrown if required element cannot be found
     */
    public List<Contract> orderContracts(AdditionalConstraint... dependencies) throws
            UnsolvableException, MissingRequiredException, DuplicatedProductionException, MultipleEnderException {

        Set<Key> required = this.toUse.getAllRequired();
        Set<Key> created = this.toUse.getAllCreated();

        required.removeAll(created);

        if(!required.isEmpty()){
            throw new MissingRequiredException(required);
        }

        checkDuplicate(toUse.getContracts());

        return order(toUse.getContracts(),dependencies);
    }

    private void checkDuplicate(List<Contract> contracts) throws DuplicatedProductionException{
        for(int i = 0 ; i< contracts.size() ; i ++){
            for(int j = 0; j < contracts.size() ; j ++){
                if (i==j) continue;
                Contract a = contracts.get(i);
                Contract b = contracts.get(j);

                for(Key key : a.getContract().getCreated()){
                    if(b.getContract().getCreated().contains(key)){
                        throw new DuplicatedProductionException(a,b);
                    }
                }


            }
        }
    }

    /**
     * use choco constraint library to order the algorithm
     * the constraints are simple :
     * all the contract consuming something you produce are after you
     * all the contract producing something you consume are before you
     * @param contracts contract to be ordered
     * @return the ordered list
     * @throws UnsolvableException if the element can't be ordered
     */
    private List<Contract> order(List<Contract> contracts, AdditionalConstraint... dependencies) throws UnsolvableException, MultipleEnderException {
        Contract[] orderedContracts = new Contract[contracts.size()];

        Model model = new Model("constraints");
        Model modelWithModifConstratins = new Model("modifConstraints");

        IntVar[] vars = new IntVar[contracts.size()];
        IntVar[] varsModif = new IntVar[contracts.size()];
        for (int i = 0; i < vars.length; i++) {
            vars[i] = model.intVar("contracts" + i, 0, vars.length - 1);
            varsModif[i] = modelWithModifConstratins.intVar("contracts" + i, 0, vars.length - 1);
        }

        Set<IntVar> toMinimize = new HashSet<>();

        for(AdditionalConstraint order : dependencies){
            order.apply(model,contracts,vars);
            order.apply(modelWithModifConstratins,contracts,varsModif);
        }

        AdditionalConstraint ender = new EndingContract(contracts);
        ender.apply(model,contracts,vars);
        ender.apply(modelWithModifConstratins,contracts,varsModif);

        for (int i = 0; i < vars.length; i++) {
            Constraints a = contracts.get(i).getContract();
            for (int j = 0; j < vars.length; j++) {
                if (i == j) continue;

                Constraints b = contracts.get(j).getContract();
                model.arithm(vars[i], "!=", vars[j]).post(); // must be different
                modelWithModifConstratins.arithm(varsModif[i], "!=", varsModif[j]).post();

                if (a.getModified().size() > 0) {
                    toMinimize.add(vars[i]);
                }


                Set<Key> requireAndModified = new HashSet<>();
                requireAndModified.addAll(a.getRequired());
                requireAndModified.addAll(a.getModified());

                for (Key required : requireAndModified) {
                    if (b.getCreated().contains(required)) {
                        model.arithm(vars[i], ">", vars[j]).post();
                        modelWithModifConstratins.arithm(varsModif[i], ">", varsModif[j]).post();
                    }
                }

                for (Key create : a.getCreated()) {
                    if (b.getRequired().contains(create)) {
                        model.arithm(vars[j], ">", vars[i]).post();
                        modelWithModifConstratins.arithm(varsModif[j], ">", varsModif[i]).post();
                    }
                }

                for (Key modif : a.getModified()) {
                    if (b.getRequired().contains(modif)) {
                        modelWithModifConstratins.arithm(varsModif[j], ">", varsModif[i]).post();
                    }
                }

            }
        }

        Solution solution = modelWithModifConstratins.getSolver().findSolution();

        if(solution != null){
            for(int i = 0;i<vars.length;i++){
                int position = solution.getIntVal(varsModif[i]);
                orderedContracts[position] =contracts.get(i);
            }

            return new ArrayList<>(Arrays.asList(orderedContracts));
        }

        solution = resolveWithMinimisation(model,toMinimize);

        //ordering the contracts
        for(int i = 0;i<vars.length;i++){
            int position = solution.getIntVal(vars[i]);
            orderedContracts[position] =contracts.get(i);
        }

        return new ArrayList<>(Arrays.asList(orderedContracts));
    }

    private Solution resolveWithMinimisation(Model model, Set<IntVar> toMinimize) throws UnsolvableException{


        if (toMinimize.size() == 1) {
            model.setObjective(Model.MINIMIZE,(IntVar)toMinimize.toArray()[0]);
        } else if (toMinimize.size() > 1) {
            ParetoOptimizer po = new ParetoOptimizer(Model.MINIMIZE,toMinimize.toArray(new IntVar[0]));
            model.getSolver().plugMonitor(po);
        }

        Solution solution = model.getSolver().findSolution();
        // if the solution is null, there is no solution

        if(solution == null){
            throw new UnsolvableException();
        }

        return solution;
    }


}
