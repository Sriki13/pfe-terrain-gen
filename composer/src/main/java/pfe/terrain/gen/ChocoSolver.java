package pfe.terrain.gen;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;
import pfe.terrain.gen.algo.constraints.Constraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ChocoSolver {

    public static void main(String[] args) {
        List<Constraints> constraints = new ArrayList<>();

        constraints.add(new Constraints(new HashSet<>(Arrays.asList("VERTEX","MESH")),
                new HashSet<>()));

        constraints.add(new Constraints(new HashSet<>(Arrays.asList("POINT")),
                new HashSet<>(Arrays.asList("EDGE"))));

        constraints.add(new Constraints(new HashSet<>(Arrays.asList()),
                new HashSet<>(Arrays.asList("POINT"))));

        constraints.add(new Constraints(new HashSet<>(Arrays.asList("EDGE")),
                new HashSet<>(Arrays.asList("MESH"))));

        constraints.add(new Constraints(new HashSet<>(Arrays.asList("EDGE")),
                new HashSet<>(Arrays.asList("VERTEX"))));



        Model model = new Model( "constraints");

        IntVar[] vars = new IntVar[constraints.size()];
        for(int i = 0; i < vars.length ; i++){
            vars[i] = model.intVar("constraint" + i,0,vars.length-1);
        }

        for(int i =0 ; i< vars.length;i++){
            Constraints a = constraints.get(i);
            for(int j = 0; j< vars.length;j++){
                if( i== j) continue;

                Constraints b = constraints.get(j);
                model.arithm(vars[i], "!=",vars[j]).post(); // must be different

                for(String required : a.getRequired()){
                    if(b.getCreated().contains(required)){
                        model.arithm(vars[i], ">",vars[j]).post();
                    }
                }

                for(String create : a.getCreated()){
                    if(b.getRequired().contains(create)){
                        model.arithm(vars[j], ">",vars[i]).post();
                    }
                }
            }
        }

        Solution solution = model.getSolver().findSolution();

        System.out.println(solution);
    }
}
